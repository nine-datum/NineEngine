package nine.geometry.collada;

import java.util.HashMap;

public class ColladaBasicMaterialParser implements ColladaMaterialParser
{
    @Override
    public void read(ColladaNode node, MaterialReader reader)
    {
        HashMap<String, String> effectToParam = new HashMap<>();
        HashMap<String, String> paramToSurface = new HashMap<>();
        HashMap<String, String> surfaceToImage = new HashMap<>();
        HashMap<String, String> imageToFile = new HashMap<>();
        HashMap<String, String> materialToEffect = new HashMap<>();

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
                    sampler.children("source", source ->
                    source.content(content ->
                    {
                        paramToSurface.put(paramId, content);
                    })));
                    newparam.children("surface", surface ->
                    surface.children("init_from", init ->
                    init.content(content ->
                    {
                        surfaceToImage.put(paramId, content);
                    })));
                }));

                profile.children("technique", tech ->
                tech.children("lambert", lambert ->
                lambert.children("diffuse", diffuse ->
                diffuse.children("texture", texture ->
                texture.attribute("texture", image ->
                {
                    effectToParam.put("#" + id, image);
                })))));
            }))));

            root.children("library_materials", lib ->
            lib.children("material", material ->
            material.attribute("id", id ->
            material.children("instance_effect", effect ->
            effect.attribute("url", url ->
            {
                materialToEffect.put(id, url);
            })))));
        });

        reader.call(name ->
        {
            return imageToFile.get(
                surfaceToImage.get(
                    paramToSurface.get(
                        effectToParam.get(
                            materialToEffect.get(name)))));
        });
    }
}