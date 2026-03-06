package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.bootstrap.BootstrapRuleResponse;
import com.example.flagsentinelapi.dto.rule.CreateRuleRequest;
import com.example.flagsentinelapi.dto.rule.RuleResponse;
import com.example.flagsentinelapi.dto.rule.UpdateRuleRequest;
import com.example.flagsentinelapi.exception.NotFoundException;
import com.example.flagsentinelapi.logging.ApiLogMessages;
import com.example.flagsentinelapi.mapper.RuleMapper;
import com.example.flagsentinelapi.model.Rule;
import com.example.flagsentinelapi.repository.RuleRepository;
import com.example.flagsentinelapi.websocket.WebSocketEventPublisher;
import com.example.flagsentinelapi.logging.LogPropertiesKeys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleService {

    private static final Logger log = LoggerFactory.getLogger(RuleService.class);

    private final RuleRepository repo;
    private final RuleMapper mapper;
    private final WebSocketEventPublisher ws;

    public RuleResponse create(CreateRuleRequest request) {

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.RULE_CREATE_REQUEST,
                request.getAttribute(),
                request.getOperator()
        ));

        Rule rule = mapper.toEntity(request);
        Rule saved = repo.save(rule);

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.RULE_CREATE_SUCCESS,
                saved.getId(),
                saved.getAttribute(),
                saved.getOperator()
        ));

        ws.publishRuleUpdate(mapper.toBootstrapDto(saved));

        return mapper.toResponse(saved);
    }

    public RuleResponse update(Long id, UpdateRuleRequest request) {

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.RULE_UPDATE_REQUEST,
                id
        ));

        Rule rule = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn(ApiLogMessages.get(
                            LogPropertiesKeys.RULE_NOT_FOUND,
                            id
                    ));
                    return new NotFoundException("Rule not found");
                });

        mapper.updateEntity(rule, request);
        Rule saved = repo.save(rule);

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.RULE_UPDATE_SUCCESS,
                saved.getId(),
                saved.getAttribute(),
                saved.getOperator()
        ));

        ws.publishRuleUpdate(mapper.toBootstrapDto(saved));

        return mapper.toResponse(saved);
    }

    public RuleResponse getById(Long id) {

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.RULE_GET_REQUEST,
                id
        ));

        Rule rule = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn(ApiLogMessages.get(
                            LogPropertiesKeys.RULE_NOT_FOUND,
                            id
                    ));
                    return new NotFoundException("Rule not found");
                });

        return mapper.toResponse(rule);
    }

    public List<RuleResponse> getAll() {

        log.debug(ApiLogMessages.get(LogPropertiesKeys.RULE_GET_ALL_REQUEST));

        List<RuleResponse> list = repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.RULE_GET_ALL_SUCCESS,
                list.size()
        ));

        return list;
    }

    public List<BootstrapRuleResponse> getAllBootstrap() {

        log.debug(ApiLogMessages.get(LogPropertiesKeys.RULE_BOOTSTRAP_REQUEST));

        List<BootstrapRuleResponse> list = repo.findAll().stream()
                .map(mapper::toBootstrapDto)
                .toList();

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.RULE_BOOTSTRAP_SUCCESS,
                list.size()
        ));

        return list;
    }

    public void delete(Long id) {

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.RULE_DELETE_REQUEST,
                id
        ));

        Rule rule = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn(ApiLogMessages.get(
                            LogPropertiesKeys.RULE_NOT_FOUND,
                            id
                    ));
                    return new NotFoundException("Rule not found");
                });

        repo.delete(rule);

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.RULE_DELETE_SUCCESS,
                id,
                rule.getAttribute()
        ));

        ws.publishRuleUpdate("Rule_deleted:" + id);
    }

    public Page<RuleResponse> findAllPaged(Pageable pageable) {

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.RULE_PAGE_REQUEST,
                pageable.getPageNumber(),
                pageable.getPageSize()
        ));

        Page<RuleResponse> page = repo.findAll(pageable)
                .map(mapper::toResponse);

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.RULE_PAGE_SUCCESS,
                page.getTotalElements()
        ));

        return page;
    }
}