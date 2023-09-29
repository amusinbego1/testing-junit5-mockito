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

    @ParameterizedTest
    @CsvSource({"1", "2", "3"})
    void delete(long toDeleteObjectKey) {
        int sizeBeforeDeletion = visitDbAsMap.size();
        Visit toDeleteObject = visitDbAsMap.get(toDeleteObjectKey);

        doAnswer(invocation -> {
            visitDbAsMap.remove(toDeleteObjectKey);
            return null;
        }).when(repository).delete(argThat(arg -> arg == toDeleteObject));

        service.delete(toDeleteObject);

        assertAll(
                () -> assertEquals(sizeBeforeDeletion-1, visitDbAsMap.size()),
                () -> assertFalse(visitDbAsMap.containsKey(toDeleteObjectKey))
        );
        verify(repository).delete(any(Visit.class));
    }

    @Test
    void deleteById() {
        var sizeBeforeDeletion = visitDbAsMap.size();
        doAnswer(invocation -> {
            Long arg = invocation.getArgument(0);
            if(arg > 3L || arg < 1L)
                throw new RuntimeException("No such element with given ID");
            visitDbAsMap.remove(arg);
            return null;
        }).when(repository).deleteById(anyLong());

        service.deleteById(1L);
        assertEquals(sizeBeforeDeletion-1, visitDbAsMap.size());
        then(repository).should().deleteById(anyLong());
    }
}