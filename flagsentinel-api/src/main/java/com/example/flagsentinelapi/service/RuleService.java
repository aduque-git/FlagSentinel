package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.bootstrap.BootstrapRuleResponse;
import com.example.flagsentinelapi.dto.rule.CreateRuleRequest;
import com.example.flagsentinelapi.dto.rule.RuleResponse;
import com.example.flagsentinelapi.dto.rule.UpdateRuleRequest;
import com.example.flagsentinelapi.exception.NotFoundException;
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

    // CREATE
    public RuleResponse create(CreateRuleRequest request) {

        Rule rule = mapper.toEntity(request);
        Rule saved = repo.save(rule);

        log.info("Rule created id={} attribute={} operator={}",
                saved.getId(),
                saved.getAttribute(),
                saved.getOperator());

        ws.publishRuleUpdate(mapper.toBootstrapDto(saved));

        return mapper.toResponse(saved);
    }

    // UPDATE
    public RuleResponse update(Long id, UpdateRuleRequest request) {

        Rule rule = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Rule not found id={}", id);
                    return new NotFoundException("Rule not found");
                });

        mapper.updateEntity(rule, request);
        Rule saved = repo.save(rule);

        log.info("Rule updated id={} attribute={} operator={}",
                saved.getId(),
                saved.getAttribute(),
                saved.getOperator());

        ws.publishRuleUpdate(mapper.toBootstrapDto(saved));

        return mapper.toResponse(saved);
    }

    // GET BY ID
    public RuleResponse getById(Long id) {

        Rule rule = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Rule not found id={}", id);
                    return new NotFoundException("Rule not found");
                });

        return mapper.toResponse(rule);
    }

    // GET ALL
    public List<RuleResponse> getAll() {

        List<RuleResponse> list = repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();

        log.debug("Retrieved {} rules", list.size());

        return list;
    }

    // BOOTSTRAP
    public List<BootstrapRuleResponse> getAllBootstrap() {

        List<BootstrapRuleResponse> list = repo.findAll().stream()
                .map(mapper::toBootstrapDto)
                .toList();

        log.debug("Retrieved {} bootstrap rules", list.size());

        return list;
    }

    // DELETE
    public void delete(Long id) {

        Rule rule = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Rule not found id={}", id);
                    return new NotFoundException("Rule not found");
                });

        repo.delete(rule);

        log.info("Rule deleted id={} attribute={}",
                id, rule.getAttribute());

        ws.publishRuleUpdate("Rule_deleted:" + id);
    }

    // PAGINATION
    public Page<RuleResponse> findAllPaged(Pageable pageable) {

        Page<RuleResponse> page = repo.findAll(pageable)
                .map(mapper::toResponse);

        log.debug("Rules page requested page={} size={} total={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                page.getTotalElements());

        return page;
    }
}