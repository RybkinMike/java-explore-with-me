package ru.practicum.ewm.model.compilation;

import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.event.Event;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class CompilationMapper {
    public Compilation toEntityFromNewComp(NewCompilationDto newCompilationDto, List<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setEvents(events);
        compilation.setPinned(newCompilationDto.getPinned());
        if (newCompilationDto.getPinned() == null) {
            compilation.setPinned(false);
        }
        compilation.setTitle(newCompilationDto.getTitle());
        return compilation;
    }

    public CompilationDto toDtoFromEntity(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setEvents(compilation.getEvents());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        return compilationDto;
    }

    public List<CompilationDto> toListDtoFromListEntity(List<Compilation> listEntity) {
        List<CompilationDto> listDto = new ArrayList<>();
        for (Compilation compilation:listEntity
        ) {
            listDto.add(toDtoFromEntity(compilation));
        }
        return listDto;
    }
}
