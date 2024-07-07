package nine.geometry;

import nine.function.RefreshStatus;

public interface AnimationSource
{
	Animation instance(RefreshStatus refreshStatus);
}
