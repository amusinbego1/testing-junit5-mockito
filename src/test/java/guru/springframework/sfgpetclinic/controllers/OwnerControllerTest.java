package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    private static final String OWNERS_CREATE_OR_UPDATE_OWNER_FORM = "owners/createOrUpdateOwnerForm";
    private static final String REDIRECT_OWNERS = "redirect:/owners/";
    public static final String FIND_OWNERS = "owners/findOwners";
    public static final String OWNERS_LIST = "owners/ownersList";
    @Mock
    private OwnerService ownerService;
    @InjectMocks
    private OwnerController controller;
    @Mock
    BindingResult bindingResult;
    List<Owner> owners;

    @BeforeEach
    void setUp() {
        owners = new ArrayList<>(List.of(
                new Owner(1L, "Amer", "Musinbegovic"),
                new Owner(2L, "Elon", "Musk"),
                new Owner(3L, "Jeff", "Bezos"),
                new Owner(4L, "Dzejna", "Musinbegovic")
        ));

    }

    @ParameterizedTest
    @CsvSource({
            "5, Zuckerberg," + FIND_OWNERS,
            "2, Musk," + REDIRECT_OWNERS + "2",
            "1, Musinbegovic," + OWNERS_LIST
    })
    void processFindFormAllCases(long id, String lastName, String result) {
        Owner owner = new Owner(id, null, lastName);
        Model mockModel = mock(Model.class);
        doAnswer(invocation -> {
            owners.removeIf(el -> !(("%" + el.getLastName() + "%").equalsIgnoreCase(invocation.getArgument(0))));
            return owners;
        }).when(ownerService).findAllByLastNameLike(anyString());

        String actual = controller.processFindForm(owner, bindingResult, mockModel);
        assertEquals(result, actual);
        verify(ownerService).findAllByLastNameLike(anyString());
    }

    @Test
    void processCreationFormWithoutErrors() {
        Owner newOwner = new Owner(2L, "Elon", "Musk");

        doAnswer(invocation -> invocation.getArgument(0)).when(ownerService).save(any(Owner.class));
        assertEquals(REDIRECT_OWNERS + newOwner.getId(), controller.processCreationForm(newOwner, bindingResult));

        verify(ownerService).save(any(Owner.class));
    }

    @Test
    void processCreationFormWithErrors() {
        Owner newOwner = new Owner(2L, "Elon", "Musk");

        doReturn(true).when(bindingResult).hasErrors();

        assertEquals(OWNERS_CREATE_OR_UPDATE_OWNER_FORM, controller.processCreationForm(newOwner, bindingResult));
    }
}