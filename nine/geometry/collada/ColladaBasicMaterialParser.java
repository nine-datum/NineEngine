package nine.geometry.collada;

import java.util.HashMap;

import nine.drawing.Color;
import nine.drawing.ColorFloatStruct;
import nine.geometry.Material;

public class ColladaBasicMaterialParser implements ColladaMaterialParser
{
    @Override
    public void read(ColladaNode node, MaterialReader reader)
    {
        HashMap<String, String> effectToParam = new HashMap<>();
        HashMap<String, String> effectToColor = new HashMap<>();
        HashMap<String, String> paramToSurface = new HashMap<>();
        HashMap<String, String> surfaceToImage = new HashMap<>();
        HashMap<String, String> imageToFile = new HashMap<>();
        HashMap<String, String> materialToEffect = new HashMap<>();
        HashMap<String, String> sceneToMaterial = new HashMap<>();

        node.children("COLLADA", root ->
        {
            root.children("library_images", lib ->
            lib.children("image", image ->
            image.attribute("id", id ->
            image.children("init_from", init ->
            {
                init.content(file ->
                {
                    imageToFile.put(id, file);
                });
                init.children("ref", ref ->
                ref.content(file ->
                {
                    imageToFile.put(id, file);
                }));
            }))));

            root.children("library_effects", lib ->
            lib.children("effect", effect ->
            effect.attribute("id", id ->
            effect.children("profile_COMMON", profile ->
            {
                profile.children("newparam", newparam ->
                newparam.attribute("sid", paramId ->
                {
                    newparam.children("sampler2D", sampler ->
                    {
                        sampler.children("source", source ->
                        source.content(content ->
                        {
                            paramToSurface.put(paramId, content);
                        }));
                        sampler.children("instance_image", instance_image ->
                        instance_image.attribute("url", image ->
                        {
                            paramToSurface.put(paramId, paramId);
                            surfaceToImage.put(paramId, image.replace("#", ""));
                        }));
                    });
                    newparam.children("surface", surface ->
                    surface.children("init_from", init ->
                    init.content(content ->
                    {
                        surfaceToImage.put(paramId, content);
                    })));
                }));

                profile.children("technique", tech ->
                tech.manyChildren(tags ->
                {
                    tags.read("lambert");
                    tags.read("blinn");
                    tags.read("phong");
                }, specific_tech ->
                specific_tech.children("diffuse", diffuse ->
                {
                	var effectId = "#" + id;
                	
                	diffuse.children("color", color ->
                	{
                		color.content(colorText -> effectToColor.put(effectId, colorText));
                	});
	                diffuse.children("texture", texture ->
	                texture.attribute("texture", image ->
	                {
	                    effectToParam.put(effectId, image);
	                }));
                })));
            }))));

            root.children("library_materials", lib ->
            lib.children("material", material ->
            material.attribute("id", id ->
            material.children("instance_effect", effect ->
            effect.attribute("url", url ->
            {
                materialToEffect.put("#" + id, url);
            })))));
            root.children("library_visual_scenes", lib ->
            lib.children("visual_scene", scene ->
            {
                class RecursiveReader implements NodeReader
                {
                    @Override
                    public void read(ColladaNode child)
                    {
                        child.children("node", this);
                        child.manyChildren(selector ->
                        {
                            selector.read("instance_controller");
                            selector.read("instance_geometry");
                        }, controller ->
                        controller.children("bind_material", material ->
                        material.children("technique_common", tech ->
                        tech.children("instance_material", instance ->
                        instance.attribute("symbol", symbol ->
                        instance.attribute("target", target ->
                        {
                            sceneToMaterial.put(symbol, target);
                        }))))));
                    }
                }
                scene.children("node", new RecursiveReader());
            }));
        });

        for(var name : sceneToMaterial.keySet())
        {
        	String effect =
    			materialToEffect.get(
					sceneToMaterial.get(name));
            String tex = imageToFile.get(
                surfaceToImage.get(
                    paramToSurface.get(
                        effectToParam.get(
                            effect))));
            String colorText = effectToColor.get(effect);
            Color color;
            
            if(tex == null) tex = "default.png";
            color = colorText == null ? new ColorFloatStruct(1, 1, 1, 1) : Color.parse(colorText);
            
            reader.call(name, tex, color);
        }
    }
}