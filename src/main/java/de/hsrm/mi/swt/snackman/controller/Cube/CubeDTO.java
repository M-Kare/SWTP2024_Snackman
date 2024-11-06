package de.hsrm.mi.swt.snackman.controller.Cube;

import de.hsrm.mi.swt.snackman.entities.Cube.Cube;

public record CubeDTO(double width, double height, double depth, double rotationAngleX, double rotationAngleY) {

    public static CubeDTO fromCube(Cube c) {
        return new CubeDTO(c.getWidth(), c.getHeight(), c.getDepth(), c.getRotationAngleX(), c.getRotationAngleY());
    }

    public static Cube toCube(CubeDTO dto) {
        Cube cube = new Cube();
        cube.setWidth(dto.width);
        cube.setHeight(dto.height);
        cube.setDepth(dto.depth);
        cube.setRotationAngleX(dto.rotationAngleX);
        cube.setRotationAngleY(dto.rotationAngleY);

        return cube;
    }

}
