package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Visit;
import guru.springframework.sfgpetclinic.repositories.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class VisitSDJpaServiceTest {

    @Mock
    private VisitRepository repository;

    @InjectMocks
    private VisitSDJpaService service;

    @Captor
    private ArgumentCaptor<Long> captor;

    private Map<Long, Visit> visitDbAsMap;

    @BeforeEach
    void setUp() {
        visitDbAsMap = new HashMap<>(Map.ofEntries(
                Map.entry(1L, new Visit()),
                Map.entry(2L, new Visit()),
                Map.entry(3L, new Visit())
        ));
    }

    @Test
    void findAll() {
        var visitSet = new HashSet<>(visitDbAsMap.values());
        when(repository.findAll()).thenReturn(visitSet);

        Set<Visit> returnedSet = service.findAll();
        assertAll(
                () -> assertEquals(visitSet.size(), returnedSet.size()),
                () -> assertTrue(visitSet.containsAll(returnedSet))
        );
        verify(repository).findAll();
    }

    @ParameterizedTest
    @CsvSource({"1", "2", "3"})
    void findById(Long ID) {
        when(repository.findById(anyLong())).thenAnswer(
                invocation -> Optional.of(visitDbAsMap.get((Long)invocation.getArgument(0)))
        );
        assertEquals(visitDbAsMap.get(ID), service.findById(ID));
        verify(repository).findById(any());
    }

    @Test
    void save() {
        when(repository.save(any(Visit.class))).thenAnswer((invocation -> {
            var visitArgument = (Visit) invocation.getArgument(0);
            visitDbAsMap.put(4L, visitArgument);
            return visitArgument;
        }));
        when(repository.findById(anyLong())).thenAnswer(
                invocation -> Optional.of(visitDbAsMap.get((Long)invocation.getArgument(0)))
        );

        assertEquals(service.save(new Visit()), service.findById(4L));
        assertTrue(visitDbAsMap.containsKey(4L));

        verify(repository).save(any(Visit.class));
    }

    @Test
    void delete() {
        service.delete(new Visit());
        verify(repository).delete(any(Visit.class));
    }

    @Test
    void deleteById() {
        service.deleteById(1L);
        verify(repository).deleteById(anyLong());
    }
}