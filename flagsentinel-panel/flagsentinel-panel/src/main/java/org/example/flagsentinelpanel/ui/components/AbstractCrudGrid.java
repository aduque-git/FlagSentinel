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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCrudGrid<T> extends VerticalLayout {

    protected final Grid<T> grid;
    protected T editingItem = null;
    protected T creatingItem = null;

    protected int page = 0;
    protected final int pageSize = 5;

    Span title;

    public AbstractCrudGrid(Class<T> clazz, Span title) {
        this.grid = new Grid<>(clazz, false);
        this.title = title;
    }

    protected void init(){


        setWidthFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        grid.addClassName("abstract-grid");
        grid.setWidth("100%");

        grid.setAllRowsVisible(true);

        grid.getEditor().setBuffered(true);

        configureColumns();
        add(buildHeader(), wrapGrid(), buildPagination());
        refresh();

    }


    protected abstract void configureColumns();

    protected abstract List<T> fetchAll();

    protected abstract void save(T item);

    protected abstract void delete(T item);

    protected abstract T createEmpty();

    protected Component buildHeader() {

        // TÍTULO
        title.getStyle().set("color", "white");
        title.getStyle().set("font-weight", "bold");
        title.getStyle().set("font-size", "1.3rem"); // un poco más grande
        title.getStyle().set("margin-right", "auto"); // empuja el botón a la derecha

        Button addButton = new Button(new Icon(VaadinIcon.PLUS));
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

    private Component buildPagination() {
        Button prev = new Button(new Icon(VaadinIcon.ANGLE_LEFT));
        prev.addClassName("inline-action");
        prev.addClickListener(e -> {
            if (page > 0) {
                page--;
                refresh();
            }
        });

        Button next = new Button(new Icon(VaadinIcon.ANGLE_RIGHT));
        next.addClassName("inline-action");
        next.addClickListener(e -> {
            page++;
            refresh();
        });

        HorizontalLayout layout = new HorizontalLayout(prev, next);
        layout.setSpacing(true);
        layout.setAlignItems(Alignment.CENTER);
        return layout;
    }

    protected void startCreate() {
        creatingItem = createEmpty();
        List<T> items = new ArrayList<>(fetchAll());
        items.add(0, creatingItem);
        grid.setItems(items);
        startEdit(creatingItem);
    }

    protected void startEdit(T item) {
        editingItem = item;
        grid.getEditor().editItem(item);
    }

    protected void cancelEdit() {
        grid.getEditor().cancel();
        if (creatingItem != null) {
            refresh();
        }
        editingItem = null;
        creatingItem = null;

        grid.getDataProvider().refreshAll();
    }

    protected boolean isEditing(T item) {
        return editingItem == item || creatingItem == item;
    }

    protected void refresh() {
        List<T> all = fetchAll();
        int from = page * pageSize;
        int to = Math.min(from + pageSize, all.size());
        if (from >= all.size()) {
            page = 0;
            from = 0;
            to = Math.min(pageSize, all.size());
        }
        grid.setItems(all.subList(from, to));
    }

    protected void showErrorNotification(String message) {
        Notification n = new Notification();
        n.setText(message);
        n.setDuration(3000);
        n.setPosition(Notification.Position.MIDDLE);
        n.addClassName("app-notification");
        n.open();
    }

    protected boolean isNullOrBlank(String s) {
        return s == null || s.isBlank();
    }
}
