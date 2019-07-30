package org.marketdesignresearch.mechlib.metainfo;

import lombok.Data;

@Data
public final class MetaInfo {
    private long javaRuntime = 0;
    private long mipSolveTime = 0;
    private long lpSolveTime = 0;
    private long qpSolveTime = 0;
    private long approxSolveTime = 0;

    private int numberOfLPs = 0;
    private int numberOfQPs = 0;
    private int numberOfApproximations = 0;
    private int numberOfMIPs = 0;
    private int constraintsGenerated = 0;
    private int ignoredConstraints = 0;

    public MetaInfo join(MetaInfo other) {
        MetaInfo newMetaInfo = new MetaInfo();
        newMetaInfo.setConstraintsGenerated(constraintsGenerated + other.constraintsGenerated);
        newMetaInfo.setLpSolveTime(lpSolveTime + other.lpSolveTime);
        newMetaInfo.setQpSolveTime(qpSolveTime + other.qpSolveTime);
        newMetaInfo.setMipSolveTime(mipSolveTime + other.mipSolveTime);
        newMetaInfo.setNumberOfMIPs(numberOfMIPs + other.numberOfMIPs);
        newMetaInfo.setNumberOfQPs(numberOfQPs + other.numberOfQPs);
        newMetaInfo.setNumberOfLPs(numberOfLPs + other.numberOfLPs);
        newMetaInfo.setNumberOfApproximations(numberOfApproximations + other.numberOfApproximations);
        newMetaInfo.setIgnoredConstraints(ignoredConstraints + other.ignoredConstraints);
        newMetaInfo.setJavaRuntime(javaRuntime + other.javaRuntime);
        newMetaInfo.setApproxSolveTime(approxSolveTime + other.approxSolveTime);

        return newMetaInfo;
    }
}
