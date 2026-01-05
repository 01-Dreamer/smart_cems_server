package zxylearn.smart_cems_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zxylearn.smart_cems_server.entity.Building;
import zxylearn.smart_cems_server.service.BuildingService;

import java.util.List;

@RestController
@RequestMapping("/building")
@Tag(name = "Building Management", description = "Building API")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @GetMapping("/list")
    @Operation(summary = "List all buildings")
    public List<Building> list() {
        return buildingService.list();
    }

    @PostMapping("/add")
    @Operation(summary = "Add building")
    public boolean add(@RequestBody Building building) {
        return buildingService.save(building);
    }
}
