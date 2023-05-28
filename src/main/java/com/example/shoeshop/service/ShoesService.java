package com.example.shoeshop.service;

import com.example.shoeshop.dto.*;
import com.example.shoeshop.entity.*;
import com.example.shoeshop.repository.AvailabilitiesRepository;
import com.example.shoeshop.repository.BrandsRepository;
import com.example.shoeshop.repository.ShoesRepository;
import com.example.shoeshop.repository.SizesRepository;
import com.example.shoeshop.util.FileUploadUtil;
import com.example.shoeshop.util.SortType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ShoesService {

    @Value("${upload.path.img}")
    private String imgPath;

    @Value("${spring.goods.count.per_page}")
    private Integer countGoodsPerPage;

    private final ShoesRepository shoesRepository;
    private final BrandsRepository brandsRepository;
    private final AvailabilitiesRepository availabilitiesRepository;
    private final SizesRepository sizesRepository;

    @Autowired
    public ShoesService(ShoesRepository shoesRepository, BrandsRepository brandsRepository, AvailabilitiesRepository availabilitiesRepository, SizesRepository sizesRepository) {
        this.shoesRepository = shoesRepository;
        this.brandsRepository = brandsRepository;
        this.availabilitiesRepository = availabilitiesRepository;
        this.sizesRepository = sizesRepository;
    }

    public Optional<BrandReadDto> findBrandById(Integer id) {
        return brandsRepository.findById(id)
                .map(this::mapFrom);
    }

    public List<BrandReadDto> findAllBrands() {
        return brandsRepository.findAll()
                .stream()
                .map(this::mapFrom)
                .toList();
    }

    public Optional<SizeReadDto> findSizeById(Integer id){
        return sizesRepository.findById(id)
                .map(this::mapFrom);
    }

    public List<ShoesReadDto> findAllShoesWithBrand(){
        return shoesRepository.findAllWithBrand()
                .stream()
                .map(this::mapFrom)
                .toList();
    }

    public List<ShoesReadDto> findAllShoesWithBrand(Integer page) {
        return shoesRepository.findAllWithBrand(page, countGoodsPerPage)
                .stream()
                .map(this::mapFrom)
                .toList();
    }

    public List<ShoesReadDto> findWithCriteria(List<String> brands, List<Gender> genders, Integer priceMin,
                                               Integer priceMax, Integer sizeMin, Integer sizeMax, SortType sortType,
                                               String isContain, Integer page){
        if (priceMin != null && priceMin <= 0) priceMin = null;
        if (sizeMin != null && sizeMin <= 20) sizeMin = null;
        if (sizeMax != null && sizeMax >= 50) sizeMax = null;
        return shoesRepository.findWithCriteria(
                        brands, genders, priceMin, priceMax, sizeMin, sizeMax, sortType,
                        isContain.equals("on"), page, countGoodsPerPage)
                .stream()
                .map(this::mapFrom)
                .toList();
    }

    public Optional<ShoesDetailsReadDto> findShoesDetailsById(Long id) {
        Shoes shoes = shoesRepository.findByIdWithBrandAndSize(id)
                .orElseThrow(() -> new IllegalArgumentException("Shoes with this id do not contain"));
        return Optional.of(new ShoesDetailsReadDto(
                shoes.getId(),
                shoes.getName(),
                shoes.getImage(),
                shoes.getDescription(),
                shoes.getGender().toString(),
                shoes.getBrand().getName(),
                shoes.getPrice(),
                shoes.getAvailabilities().stream()
                        .filter(a -> a.getCount() > 0)
                        .map(a -> new SizeReadDto(a.getSize().getId(), a.getSize().getSize()))
                        .sorted(Comparator.comparing(SizeReadDto::getSize))
                        .toList()
        ));
    }

    public ShoesUpdateDto findByIdForUpdate(Long id){
        Shoes shoes = shoesRepository.findByIdWithBrandAndSize(id)
                .orElseThrow(() -> new IllegalArgumentException("Shoes with this id do not contain"));
        return new ShoesUpdateDto(
                shoes.getId(),
                shoes.getName(),
                shoes.getDescription(),
                shoes.getGender().toString(),
                shoes.getBrand().getId(),
                shoes.getPrice(),
                shoes.getAvailabilities().stream()
                        .map(a -> new AvailabilityCreateDto(a.getSize().getId(), a.getCount()))
                        .sorted(Comparator.comparing(AvailabilityCreateDto::getSizeId))
                        .toList()
        );
    }

    @Transactional
    public void save(ShoesCreateDto dto) throws IOException {
        Brand brand = dto.getBrand() == -1
                ? brandsRepository.save(new Brand(dto.getNewBrand()))
                : brandsRepository.getReferenceById(dto.getBrand());

        String fileName = createFile(dto.getImage());

        Shoes shoes = Shoes.builder()
                .name(dto.getName())
                .image(fileName)
                .description(dto.getDescription())
                .gender(Gender.valueOf(dto.getGender()))
                .price(dto.getPrice())
                .brand(brand)
                .build();

        shoesRepository.save(shoes);

        dto.getAvailabilities().stream()
                .filter(a -> a.getSizeId() != -1)
                .map(a -> new Availability(
                        shoes,
                        sizesRepository.getReferenceById(a.getSizeId()),
                        a.getCount()))
                .forEach(availabilitiesRepository::save);
    }

    @Transactional
    public void update(ShoesUpdateDto dto) throws IOException {
        Shoes shoes = shoesRepository.findById(dto.getId()).orElseThrow(IllegalArgumentException::new);
        shoes.setName(dto.getName());
        shoes.setDescription(dto.getDescription());
        shoes.setGender(Gender.valueOf(dto.getGender()));
        shoes.setPrice(dto.getPrice());
        shoes.setBrand(
                dto.getBrand() != -1
                ? brandsRepository.getReferenceById(dto.getBrand())
                : brandsRepository.save(new Brand(dto.getNewBrand()))
        );

        if (dto.getIsNewImage() == 1){
            FileUploadUtil.delete(imgPath, shoes.getImage());
            shoes.setImage(createFile(dto.getImage()));
        }

        shoesRepository.save(shoes);

        Map<Integer, Availability> availabilities = shoes
                .getAvailabilities()
                .stream()
                .collect(Collectors.toMap(a -> a.getSize().getId(), a -> a));

        Map<Integer, AvailabilityCreateDto> aFromDto = dto.getAvailabilities()
                .stream()
                .filter(a -> a.getCount() != null && a.getSizeId() != -1)
                .collect(Collectors.toMap(AvailabilityCreateDto::getSizeId, a -> a));

        for (Integer i : availabilities.keySet()) {
            if (!aFromDto.containsKey(i))
                availabilitiesRepository.delete(availabilities.get(i));
            else {
                Availability a = availabilities.get(i);
                a.setCount(aFromDto.get(i).getCount());
                a.setSize(sizesRepository.getReferenceById(aFromDto.get(i).getSizeId()));
                availabilitiesRepository.save(a);
                aFromDto.remove(i);
            }
        }

        aFromDto.values().stream()
                .map(a -> new Availability(
                        shoes,
                        sizesRepository.getReferenceById(a.getSizeId()),
                        a.getCount()
                ))
                .forEach(availabilitiesRepository::save);
    }

    @Transactional
    public void delete(Long id) throws IOException {
        Optional<Shoes> shoes = shoesRepository.findById(id);
        if (shoes.isEmpty())
            throw new IllegalArgumentException("This id did not find");

        FileUploadUtil.delete(imgPath, shoes.get().getImage());
        shoesRepository.delete(shoes.get());
    }

    private String createFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "." + file.getOriginalFilename();
        FileUploadUtil.save(imgPath, fileName, file);
        return fileName;
    }

    public List<SizeReadDto> findAllSizes() {
        return sizesRepository.findAll()
                .stream()
                .map(this::mapFrom)
                .toList();
    }

    private BrandReadDto mapFrom(Brand brand) {
        return new BrandReadDto(
                brand.getId(),
                brand.getName()
        );
    }

    private SizeReadDto mapFrom(Size size) {
        return new SizeReadDto(
                size.getId(),
                size.getSize()
        );
    }

    private ShoesReadDto mapFrom(Shoes shoes) {
        return new ShoesReadDto(
                shoes.getId(),
                shoes.getName(),
                shoes.getBrand().getName(),
                shoes.getPrice(),
                shoes.getImage(),
                shoes.getAvailabilities().stream().anyMatch(availability -> availability.getCount() > 0)
        );
    }
}
