package com.kardan.api.controller;

import com.kardan.api.dto.*;
import com.kardan.api.model.*;
import com.kardan.api.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/kardan")
public class GuestController {

    private final BrandService brandService;
    private final ModelService modelService;
    private final GenServise genServise;
    private final EngineService engineService;
    private final CategoryService categoryService;
    private final UnitService unitService;
    private final PartService partService;
    private final ModelMapper modelMapper;

    public GuestController(BrandService brandService, ModelService modelService, GenServise genServise, EngineService engineService,
                           CategoryService categoryService, UnitService unitService, PartService partService, ModelMapper modelMapper) {
        this.brandService = brandService;
        this.modelService = modelService;
        this.genServise = genServise;
        this.engineService = engineService;
        this.categoryService = categoryService;
        this.unitService = unitService;
        this.partService = partService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/brands")
    public List<BrandDTO> getBrands() {
        return brandService.findAll().stream().map(this::convertBrand).collect((Collectors.toList()));
    }

    @GetMapping("/brands/model")
    public List<ModelDTO> getModels(@RequestParam("brandId") int id) {
        return modelService.findByBrandId(id).stream().map(this::convertModel).collect((Collectors.toList()));
    }

    @GetMapping("/brands/model/gen")
    public List<GenDTO> getGens(@RequestParam("modelId") int id) {
        return genServise.findByModelId(id).stream().map(this::convertGen).collect((Collectors.toList()));
    }

    @GetMapping("/brands/model/gen/engine")
    public EngineCategoryDTO getEngines(@RequestParam("genId") int id) {
        EngineCategoryDTO engineCategoryDTO = new EngineCategoryDTO();
        engineCategoryDTO.engines = engineService.findByGen(id).stream().map(this::convertEngine).collect((Collectors.toList()));
        engineCategoryDTO.categories = categoryService.findSubCategories().stream().map(this::convertCategory).collect((Collectors.toList()));
        return engineCategoryDTO;
    }


    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody UnitDTO unitDTO) {
        unitService.save(convertUnitDTOtoUnit(unitDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @PostMapping("/{unitId}")
    public ResponseEntity<HttpStatus> update(@RequestBody UnitDTO unitDTO, @PathVariable("unitId") int id) {
        unitService.update(id, convertUnitDTOtoUnit(unitDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @PostMapping("/sold/{unitId}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("unitId") int id) {
        unitService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @GetMapping("/brands/model/gen/engine/units")
    public List<UnitDTO> getUnits(@RequestParam("categoryId") int category_id, @RequestParam("engineId") int engine_id) {

        List<Unit> foundedUnits = unitService.findUnitByPartId(partService.findPartid(category_id, engine_id));

        return convertUnitToUnitDTO(foundedUnits);
    }


    private List<UnitDTO> convertUnitToUnitDTO(List<Unit> units) {

        List<UnitDTO> unitDTOS = new ArrayList<>();

        for (Unit unit : units) {
            Part part = unit.getPart();

            Shop shop = unit.getShop();

            Manufacturer manufacturer = unit.getManufacturer();

            UnitDTO unitDTO = modelMapper.map(unit, UnitDTO.class);

            unitDTO.setPartDTO(convertPart(part));
            unitDTO.setShopDTO(convertShop(shop));
            unitDTO.setManufacturerDTO(convertManufacturer(manufacturer));

            unitDTOS.add(unitDTO);
        }
        return unitDTOS;
    }

    private Unit convertUnitDTOtoUnit(UnitDTO unitDTO) {

        PartDTO partDTO = unitDTO.getPartDTO();

        ShopDTO shopDTO = unitDTO.getShopDTO();

        ManufacturerDTO manufacturerDTO = unitDTO.getManufacturerDTO();

        double price = unitDTO.getPrice();

        Unit unit = new Unit();

        unit.setPart(partService.findPart(partDTO.getCategoryId(), partDTO.getEngineId()));
        unit.setShop(convertShopDTOtoShop(shopDTO));
        unit.setManufacturer(convertManufacturerDTOtoManufacturer(manufacturerDTO));
        unit.setPrice(price);
        return unit;
    }

    private Shop convertShopDTOtoShop(ShopDTO shopDTO) {
        return modelMapper.map(shopDTO, Shop.class);
    }

    private Manufacturer convertManufacturerDTOtoManufacturer(ManufacturerDTO manufacturerDTO) {
        return modelMapper.map(manufacturerDTO, Manufacturer.class);
    }

    private PartDTO convertPart(Part part) {
        return modelMapper.map(part, PartDTO.class);
    }

    private ShopDTO convertShop(Shop shop) {
        return modelMapper.map(shop, ShopDTO.class);
    }

    private ManufacturerDTO convertManufacturer(Manufacturer manufacturer) {
        return modelMapper.map(manufacturer, ManufacturerDTO.class);
    }

    private BrandDTO convertBrand(CommonEntity optional) {
        return modelMapper.map(optional, BrandDTO.class);
    }

    private ModelDTO convertModel(CommonEntity optional) {
        return modelMapper.map(optional, ModelDTO.class);
    }

    private GenDTO convertGen(CommonEntity optional) {
        return modelMapper.map(optional, GenDTO.class);
    }

    private EngineDTO convertEngine(Engine engine) {
        return modelMapper.map(engine, EngineDTO.class);
    }

    private CategoryDTO convertCategory(Category category) {
        return modelMapper.map(category, CategoryDTO.class);
    }

}