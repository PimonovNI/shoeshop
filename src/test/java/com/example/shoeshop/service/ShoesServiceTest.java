package com.example.shoeshop.service;

import com.example.shoeshop.dto.AvailabilityCreateDto;
import com.example.shoeshop.dto.ShoesCreateDto;
import com.example.shoeshop.dto.ShoesUpdateDto;
import com.example.shoeshop.entity.*;
import com.example.shoeshop.repository.AvailabilitiesRepository;
import com.example.shoeshop.repository.BrandsRepository;
import com.example.shoeshop.repository.ShoesRepository;
import com.example.shoeshop.repository.SizesRepository;
import com.example.shoeshop.util.FileUploadUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ShoesServiceTest {

    private final ShoesService shoesService;

    @Autowired
    ShoesServiceTest(ShoesService shoesService) {
        this.shoesService = shoesService;
    }

    @MockBean
    private ShoesRepository shoesRepository;

    @MockBean
    private BrandsRepository brandsRepository;

    @MockBean
    private AvailabilitiesRepository availabilitiesRepository;

    @MockBean
    private SizesRepository sizesRepository;

    @Test
    void saveTest1() throws IOException {
        String name = "testName";
        MultipartFile file = new MockMultipartFile("name", (byte[]) null);
        ShoesCreateDto dto = new ShoesCreateDto(
                name,
                file,
                "testDescription",
                "MAN",
                2,
                null,
                10000.,
                List.of(new AvailabilityCreateDto(1, 1), new AvailabilityCreateDto(2, 1))
        );

        Mockito.doReturn(new Brand(0, "new", null))
                .when(brandsRepository)
                .save(ArgumentMatchers.any(Brand.class));
        Mockito.doReturn(new Brand(1, "old", null))
                .when(brandsRepository)
                .getReferenceById(ArgumentMatchers.any(Integer.class));

        try (MockedStatic<FileUploadUtil> mock = Mockito.mockStatic(FileUploadUtil.class)) {
            mock.when(() -> FileUploadUtil.save(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(file)))
                    .thenAnswer(Answers.RETURNS_DEFAULTS);
        }

        shoesService.save(dto);

        ArgumentCaptor<Shoes> shoesCaptor = ArgumentCaptor.forClass(Shoes.class);

        Mockito.verify(shoesRepository, Mockito.times(1))
                .save(shoesCaptor.capture());

        Shoes shoes = shoesCaptor.getValue();
        Assertions.assertEquals(name, shoes.getName());
        Assertions.assertEquals(Gender.MAN, shoes.getGender());
        Assertions.assertEquals(1, shoes.getBrand().getId());

        Mockito.verify(availabilitiesRepository, Mockito.times(2))
                .save(ArgumentMatchers.any(Availability.class));
    }

    @Test
    void saveTest2() throws IOException {
        String name = "testName";
        MultipartFile file = new MockMultipartFile("name", (byte[]) null);
        ShoesCreateDto dto = new ShoesCreateDto(
                name,
                file,
                "testDescription",
                "WOMAN",
                -1,
                "testNewBrand",
                10000.,
                List.of(new AvailabilityCreateDto(-1, 1))
        );

        Mockito.doReturn(new Brand(0, "new", null))
                .when(brandsRepository)
                .save(ArgumentMatchers.any(Brand.class));
        Mockito.doReturn(new Brand(1, "old", null))
                .when(brandsRepository)
                .getReferenceById(ArgumentMatchers.any(Integer.class));

        try (MockedStatic<FileUploadUtil> mock = Mockito.mockStatic(FileUploadUtil.class)) {
            mock.when(() -> FileUploadUtil.save(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(file)))
                    .thenAnswer(Answers.RETURNS_DEFAULTS);
        }

        shoesService.save(dto);

        ArgumentCaptor<Shoes> shoesCaptor = ArgumentCaptor.forClass(Shoes.class);

        Mockito.verify(shoesRepository, Mockito.times(1))
                .save(shoesCaptor.capture());

        Shoes shoes = shoesCaptor.getValue();
        Assertions.assertEquals(name, shoes.getName());
        Assertions.assertEquals(Gender.WOMAN, shoes.getGender());
        Assertions.assertEquals(0, shoes.getBrand().getId());

        Mockito.verify(availabilitiesRepository, Mockito.times(0))
                .save(ArgumentMatchers.any(Availability.class));
    }

    @Test
    void updateTest1() throws IOException {
        String name = "testName";
        MultipartFile file = new MockMultipartFile("name", (byte[]) null);
        ShoesUpdateDto dto = new ShoesUpdateDto(
                0L,
                name,
                file,
                "testDescription",
                "WOMAN",
                2,
                null,
                10000.,
                List.of(new AvailabilityCreateDto(1, 1), new AvailabilityCreateDto(2, 1),
                        new AvailabilityCreateDto(3, 1), new AvailabilityCreateDto(4, 1),
                        new AvailabilityCreateDto(5, 1), new AvailabilityCreateDto(6, 1)),
                (byte) 0
        );
        Shoes shoes = new Shoes();
        shoes.setImage("image");
        shoes.setAvailabilities(List.of(
                new Availability(new Size(1, 1, null, null), 1),
                new Availability(new Size(2, 1, null, null), 1),
                new Availability(new Size(10, 1, null, null), 1)
        ));

        Mockito.doReturn(new Brand(0, "new", null))
                .when(brandsRepository)
                .save(ArgumentMatchers.any(Brand.class));
        Mockito.doReturn(new Brand(1, "old", null))
                .when(brandsRepository)
                .getReferenceById(ArgumentMatchers.any(Integer.class));
        Mockito.doReturn(Optional.of(shoes))
                .when(shoesRepository)
                .findById(ArgumentMatchers.anyLong());

        try (MockedStatic<FileUploadUtil> mock = Mockito.mockStatic(FileUploadUtil.class)) {
            mock.when(() -> FileUploadUtil.save(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(file)))
                    .thenAnswer(Answers.RETURNS_DEFAULTS);
            mock.when(() -> FileUploadUtil.delete(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                    .thenAnswer(Answers.RETURNS_DEFAULTS);
        }

        shoesService.update(dto);

        Assertions.assertEquals(name, shoes.getName());
        Assertions.assertEquals(Gender.WOMAN, shoes.getGender());
        Assertions.assertEquals(1, shoes.getBrand().getId());

        Mockito.verify(availabilitiesRepository, Mockito.times(6))
                .save(ArgumentMatchers.any(Availability.class));
        Mockito.verify(availabilitiesRepository, Mockito.times(1))
                .delete(ArgumentMatchers.any(Availability.class));
    }

    @Test
    void updateTest2() throws IOException {
        String name = "testName";
        MultipartFile file = new MockMultipartFile("name", (byte[]) null);
        ShoesUpdateDto dto = new ShoesUpdateDto(
                0L,
                name,
                file,
                "testDescription",
                "CHILD",
                -1,
                "new test name",
                10000.,
                List.of(new AvailabilityCreateDto(2, 1), new AvailabilityCreateDto(3, 1)),
                (byte) 0
        );
        Shoes shoes = new Shoes();
        shoes.setImage("image");
        shoes.setAvailabilities(List.of(
                new Availability(new Size(1, 1, null, null), 1),
                new Availability(new Size(10, 1, null, null), 1),
                new Availability(new Size(11, 1, null, null), 1)
        ));

        Mockito.doReturn(new Brand(0, "new", null))
                .when(brandsRepository)
                .save(ArgumentMatchers.any(Brand.class));
        Mockito.doReturn(new Brand(1, "old", null))
                .when(brandsRepository)
                .getReferenceById(ArgumentMatchers.any(Integer.class));
        Mockito.doReturn(Optional.of(shoes))
                .when(shoesRepository)
                .findById(ArgumentMatchers.anyLong());

        try (MockedStatic<FileUploadUtil> mock = Mockito.mockStatic(FileUploadUtil.class)) {
            mock.when(() -> FileUploadUtil.delete(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                    .thenAnswer(Answers.RETURNS_DEFAULTS);
            mock.when(() -> FileUploadUtil.save(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(file)))
                    .thenAnswer(Answers.RETURNS_DEFAULTS);
        }

        shoesService.update(dto);

        Assertions.assertEquals(name, shoes.getName());
        Assertions.assertEquals(Gender.CHILD, shoes.getGender());
        Assertions.assertEquals(0, shoes.getBrand().getId());

        Mockito.verify(availabilitiesRepository, Mockito.times(2))
                .save(ArgumentMatchers.any(Availability.class));
        Mockito.verify(availabilitiesRepository, Mockito.times(3))
                .delete(ArgumentMatchers.any(Availability.class));
    }
}