package nine.game;

public interface HumanStates
{
    HumanState walk();
    HumanState idle();
    HumanState attackLight(); 
    HumanState attackHeavy(); 
}