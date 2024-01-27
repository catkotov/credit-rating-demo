package org.cat.eye.credit.rating.model.omni.request;

public record Participant(
        String surname,
        String name,
        String patronymic,
        Contact contact
) {
}
