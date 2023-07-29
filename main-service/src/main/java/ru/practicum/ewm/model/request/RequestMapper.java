package ru.practicum.ewm.model.request;

import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class RequestMapper {

    static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ParticipationRequestDto toParticipationRequestDtoFromRequest(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setId(request.getId());
        participationRequestDto.setCreated(request.getCreated().format(dateTimeFormatter));
        participationRequestDto.setEvent(request.getEvent().getId());
        participationRequestDto.setRequester(request.getRequester().getId());
        participationRequestDto.setStatus(request.getStatus());
        return participationRequestDto;
    }

    public List<ParticipationRequestDto> toListParticipationRequestDtoFromListRequest(List<Request> listRequester) {
        List<ParticipationRequestDto> listParticipationRequestDto = new ArrayList<>();
        for (Request request:listRequester
             ) {
            listParticipationRequestDto.add(toParticipationRequestDtoFromRequest(request));
        }
        return listParticipationRequestDto;
    }
}
