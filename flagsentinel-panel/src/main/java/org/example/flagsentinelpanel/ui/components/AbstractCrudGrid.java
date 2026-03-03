package org.example.flagsentinelpanel.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.example.flagsentinelpanel.config.AppPropertyKeys;
import org.example.flagsentinelpanel.dto.PageResponse;
import org.example.flagsentinelpanel.util.AppProperties;
import org.example.flagsentinelpanel.util.SpringContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCrudGrid<T> extends VerticalLayout {

    protected final Grid<T> grid;
    protected T editingItem = null;
    protected T creatingItem = null;

    // ============================
    // PAGINACIÓN REAL
    // ============================
    protected int apiPage = 0;
    protected int pageSize;

    protected int totalElements = 0;

    Span title;

    protected Button addButton;
    protected Button prevButton;
    protected Button nextButton;

    protected final Map<Object, List<Button>> rowButtons = new HashMap<>();

    public AbstractCrudGrid(Class<T> clazz, Span title) {
        this.grid = new Grid<>(clazz, false);
        this.title = title;
    }

    protected void init() {

        setWidthFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        pageSize = SpringContext.getBean(AppProperties.class)
                .getInt(AppPropertyKeys.PAGINATION_API_PAGE_SIZE);

        grid.addClassName("abstract-grid");
        grid.setWidth("100%");
        grid.setAllRowsVisible(true);
        grid.getEditor().setBuffered(true);

        grid.getEditor().addOpenListener(e -> disableAllButtonsExcept(e.getItem()));
        grid.getEditor().addCloseListener(e -> enableAllButtons());

        configureColumns();
        add(buildHeader(), wrapGrid(), buildPagination());

        loadPage(); // CARGA INICIAL
    }

    // Métodos abstractos
    protected abstract void configureColumns();
    protected abstract PageResponse<T> fetchPage(int apiPage, int pageSize);
    protected abstract void save(T item);
    protected abstract void delete(T item);
    protected abstract T createEmpty();


    // ============================
    // PAGINACIÓN REAL
    // ============================

    protected void loadPage() {
        PageResponse<T> response = fetchPage(apiPage, pageSize);

        totalElements = response.getTotalElements();
        grid.setItems(response.getContent());

        updateButtons();
    }

    protected void updateButtons() {
        prevButton.setEnabled(apiPage > 0);

        int maxPage = (int) Math.ceil((double) totalElements / pageSize) - 1;
        nextButton.setEnabled(apiPage < maxPage);
    }

    // ============================
    // AJUSTES PROFESIONALES
    // ============================

    protected void adjustPageAfterDelete() {

        // 1. Cargar la página actual
        PageResponse<T> response = fetchPage(apiPage, pageSize);

        // 2. Si está vacía y no es la primera página → retroceder
        if (response.getContent().isEmpty() && apiPage > 0) {
            apiPage--;
            response = fetchPage(apiPage, pageSize);
        }

        // 3. Actualizar totalElements y grid
        totalElements = response.getTotalElements();
        grid.setItems(response.getContent());

        updateButtons();
    }


    protected void goToPageOfNewElement() {
        int maxPage = (int) Math.ceil((double) totalElements / pageSize) - 1;
        apiPage = maxPage;
        loadPage();
    }

    // ============================
    // BOTONES DE PAGINACIÓN
    // ============================

    private Component buildPagination() {
        prevButton = new Button(new Icon(VaadinIcon.ANGLE_LEFT));
        prevButton.addClassName("inline-action");
        prevButton.addClickListener(e -> {
            if (apiPage > 0) {
                apiPage--;
                loadPage();
            }
        });

        nextButton = new Button(new Icon(VaadinIcon.ANGLE_RIGHT));
        nextButton.addClassName("inline-action");
        nextButton.addClickListener(e -> {
            apiPage++;
            loadPage();
        });

        HorizontalLayout layout = new HorizontalLayout(prevButton, nextButton);
        layout.setSpacing(true);
        layout.setAlignItems(Alignment.CENTER);
        return layout;
    }

    // ============================
    // CRUD
    // ============================

    protected void startCreate() {
        creatingItem = createEmpty();
        grid.setItems(List.of(creatingItem));
        startEdit(creatingItem);
    }

    protected void startEdit(T item) {
        editingItem = item;
        grid.getEditor().editItem(item);
    }

    protected void cancelEdit() {

        // 1. Cerrar editor
        grid.getEditor().cancel();

        // 2. Recargar SIEMPRE la página actual
        loadPage();

        // 3. Resetear estado
        editingItem = null;
        creatingItem = null;
    }


    protected boolean isEditing(T item) {
        return editingItem == item || creatingItem == item;
    }

    protected void refresh() {
        loadPage();
    }

    // ============================
    // UI
    // ============================

    protected Component buildHeader() {

        title.getStyle().set("color", "white");
        title.getStyle().set("font-weight", "bold");
        title.getStyle().set("font-size", "1.3rem");
        title.getStyle().set("margin-right", "auto");

        addButton = new Button(new Icon(VaadinIcon.PLUS));
        addButton.addClassName("inline-action");
        addButton.getStyle().set("background", "#28a745");
        addButton.getStyle().set("color", "white");
        addButton.addClickListener(e -> startCreate());

        HorizontalLayout header = new HorizontalLayout(title, addButton);
        header.setWidth("70%");
        header.setJustifyContentMode(JustifyContentMode.END);
        return header;
    }

    private Component wrapGrid() {
        HorizontalLayout wrapper = new HorizontalLayout(grid);
        wrapper.setWidth("70%");
        wrapper.setHeightFull();
        wrapper.setJustifyContentMode(JustifyContentMode.CENTER);
        wrapper.setAlignItems(Alignment.START);
        return wrapper;
    }

    protected void showErrorNotification(String message) {
        Notification n = new Notification();
        n.setText(message);
        n.setDuration(2000);
        n.setPosition(Notification.Position.MIDDLE);
        n.addThemeName("app-notification");
        n.open();
    }

    // ============================
    // BOTONES DE FILA
    // ============================

    protected void disableAllButtonsExcept(Object editingItem) {

        if (addButton != null) addButton.setEnabled(false);
        if (prevButton != null) prevButton.setEnabled(false);
        if (nextButton != null) nextButton.setEnabled(false);

        rowButtons.forEach((item, buttons) -> {
            boolean isEditingRow = item.equals(editingItem);
            buttons.forEach(b -> b.setEnabled(isEditingRow));
        });
    }

    protected void enableAllButtons() {

        if (addButton != null) addButton.setEnabled(true);
        if (prevButton != null) prevButton.setEnabled(true);
        if (nextButton != null) nextButton.setEnabled(true);

        rowButtons.values().forEach(list -> list.forEach(b -> b.setEnabled(true)));
    }

    protected boolean isNullOrBlank(String s) {
        return s == null || s.isBlank();
    }
}
