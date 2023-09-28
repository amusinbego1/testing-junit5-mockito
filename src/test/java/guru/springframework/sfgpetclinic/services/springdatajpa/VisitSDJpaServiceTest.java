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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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
        //given
        var visitSet = new HashSet<>(visitDbAsMap.values());
        given(repository.findAll()).willReturn(visitSet);

        //when
        Set<Visit> returnedSet = service.findAll();

        //then
        assertAll(
                () -> assertEquals(visitSet.size(), returnedSet.size()),
                () -> assertTrue(visitSet.containsAll(returnedSet))
        );
        then(repository).should(atMostOnce()).findAll();
    }

    @ParameterizedTest
    @CsvSource({"1", "2", "3"})
    void findById(Long ID) {
        //given
        given(repository.findById(anyLong())).willAnswer(
                invocation -> Optional.of(visitDbAsMap.get((Long)invocation.getArgument(0)))
        );

        //when,then
        assertEquals(visitDbAsMap.get(ID), service.findById(ID));
        then(repository).should().findById(anyLong());
    }

    @Test
    void save() {
        //given
        given(repository.save(any(Visit.class))).willAnswer((invocation -> {
            var visitArgument = (Visit) invocation.getArgument(0);
            visitDbAsMap.put(4L, visitArgument);
            return visitArgument;
        }));
        given(repository.findById(anyLong())).willAnswer(
                invocation -> Optional.of(visitDbAsMap.get((Long)invocation.getArgument(0)))
        );

        //when,then
        assertEquals(service.save(new Visit()), service.findById(4L));
        assertTrue(visitDbAsMap.containsKey(4L));

        then(repository).should().save(any(Visit.class));
    }

    @Test
    void delete() {
        //given,when
        service.delete(new Visit());

        //then
        then(repository).should().delete(any(Visit.class));
    }

    @Test
    void deleteById() {
        //given,when
        service.deleteById(1L);

        //then
        then(repository).should().deleteById(anyLong());
    }
}