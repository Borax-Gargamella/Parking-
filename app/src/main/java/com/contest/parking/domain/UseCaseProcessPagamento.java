package com.contest.parking.domain;

import com.contest.parking.data.repository.StoricoRepository;

public class UseCaseProcessPagamento {

    private StoricoRepository storicoRepository;

    public interface PaymentCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public UseCaseProcessPagamento(StoricoRepository repository) {
        this.storicoRepository = repository;
    }

    public void processPayment(String idStorico, PaymentCallback callback) {
        storicoRepository.updatePagato(idStorico, true)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e));
    }

}
