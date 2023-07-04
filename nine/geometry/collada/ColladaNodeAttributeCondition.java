package nine.geometry.collada;

import nine.function.Condition;

public class ColladaNodeAttributeCondition implements Condition<ColladaNode>
{
    String name;
    String value;

    public ColladaNodeAttributeCondition(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean match(ColladaNode item)
    {
        boolean[] match = { false };
        item.attribute(name, value ->
        {
            if(this.value == value) match[0] = true;
        });
        return match[0];
    }
}