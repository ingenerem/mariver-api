package com.mariver.income.source;

import com.mariver.income.source.dto.IncomeSourceRequest;
import com.mariver.income.source.dto.IncomeSourceResponse;
import com.mariver.user.User;
import com.mariver.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomeSourceService {

    private final IncomeSourceRepository incomeSourceRepository;
    private final UserRepository userRepository;

    public IncomeSourceService( IncomeSourceRepository incomeSourceRepository,
                                UserRepository userRepository)
    {
        this.incomeSourceRepository = incomeSourceRepository;
        this.userRepository = userRepository;
    }

    public List<IncomeSourceResponse> getActiveIncomeSources(String email) {
        User user = findUserByEmail(email);

        return incomeSourceRepository.findByUserAndActiveTrue(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public IncomeSourceResponse createIncomeSource(String email, IncomeSourceRequest request) {
        User user = findUserByEmail(email);

        IncomeSource incomeSource = new IncomeSource(user, request.category(), request.description(),
                request.amount(), request.frequency());

        IncomeSource savedIncomeSource = incomeSourceRepository.save(incomeSource);

        return toResponse(savedIncomeSource);
    }

    @Transactional
    public IncomeSourceResponse updateIncomeSource(
            String email,
            Long incomeSourceId,
            IncomeSourceRequest request
    ) {
        User user = findUserByEmail(email);

        IncomeSource incomeSource = incomeSourceRepository.findById(incomeSourceId)
                .orElseThrow(() -> new RuntimeException("Income source not found"));

        if (!incomeSource.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Income source does not belong to user");
        }

        incomeSource.setCategory(request.category());
        incomeSource.setDescription(request.description());
        incomeSource.setAmount(request.amount());
        incomeSource.setFrequency(request.frequency());

        return toResponse(incomeSource);
    }

    @Transactional
    public void deactivateIncomeSource(String email, Long incomeSourceId) {
        User user = findUserByEmail(email);

        IncomeSource incomeSource = incomeSourceRepository.findById(incomeSourceId)
                .orElseThrow(() -> new RuntimeException("Income source not found"));

        if (!incomeSource.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Income source does not belong to user");
        }

        incomeSource.deactivate();
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private IncomeSourceResponse toResponse(IncomeSource incomeSource) {
        return new IncomeSourceResponse(
                incomeSource.getId(),
                incomeSource.getCategory(),
                incomeSource.getDescription(),
                incomeSource.getAmount(),
                incomeSource.getFrequency(),
                incomeSource.isActive()
        );
    }
}