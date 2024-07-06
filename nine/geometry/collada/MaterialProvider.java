package nine.geometry.collada;

import nine.geometry.Material;

public interface MaterialProvider
{
    Material properties(String materialName);
}