package nine.geometry;

import java.util.Map;

public interface MaterialProvider
{
    Material material(String materialName);
    
    static MaterialProvider ofMap(Map<String, Material> map)
    {
    	return map::get;
    }
    static MaterialProvider ofMap(Map<String, Material> map, Material defaultMaterial)
    {
    	return some(map::get, defaultMaterial);
    }
    static MaterialProvider some(MaterialProvider materials, Material defaultMaterial)
    {
    	return name ->
    	{
	    	var mat = materials.material(name);
	    	return mat == null ? defaultMaterial : mat;
    	};
    }
    static MaterialProvider some(Iterable<MaterialProvider> materials, Material defaultMaterial)
    {
    	return name ->
    	{
	    	for(var mp : materials)
	    	{
	    		var mat = mp.material(name);
	    		if(mat != null)
	    		{
	    			return mat;
	    		}
	    	}
	    	return defaultMaterial;
    	};
    }
}