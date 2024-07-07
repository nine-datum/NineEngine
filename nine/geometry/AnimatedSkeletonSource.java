package nine.geometry;

import nine.function.RefreshStatus;

public interface AnimatedSkeletonSource
{
	AnimatedSkeleton instance(RefreshStatus refreshStatus);
}