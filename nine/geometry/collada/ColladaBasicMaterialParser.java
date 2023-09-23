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

        reader.call(name ->
        {
            String tex = imageToFile.get(
                surfaceToImage.get(
                    paramToSurface.get(
                        effectToParam.get(
                            materialToEffect.get(
                                sceneToMaterial.get(name))))));
            if(tex == null) return "default.png";
            return tex;
        });
    }
}