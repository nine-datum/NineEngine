package nine.game;

public interface HumanStates
{
    HumanState walk();
    HumanState idle();
    HumanState weaponWalk();
    HumanState weaponIdle();
    HumanState attackLight(); 
    HumanState attackHeavy(); 
}