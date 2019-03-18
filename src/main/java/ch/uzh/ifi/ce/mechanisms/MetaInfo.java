package ch.uzh.ifi.ce.mechanisms;

public class MetaInfo {
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

    public long getApproxSolveTime() {
        return approxSolveTime;
    }

    public void setApproxSolveTime(long approxSolveTime) {
        this.approxSolveTime = approxSolveTime;
    }

    public int getNumberOfApproximations() {
        return numberOfApproximations;
    }

    public void setNumberOfApproximations(int numberOfApproximations) {
        this.numberOfApproximations = numberOfApproximations;
    }

    public long getJavaRuntime() {
        return javaRuntime;
    }

    public void setJavaRuntime(long javaRuntime) {
        this.javaRuntime = javaRuntime;
    }

    public long getMipSolveTime() {
        return mipSolveTime;
    }

    public void setMipSolveTime(long mipSolveTime) {
        this.mipSolveTime = mipSolveTime;
    }

    public long getLpSolveTime() {
        return lpSolveTime;
    }

    public void setLpSolveTime(long lpSolveTime) {
        this.lpSolveTime = lpSolveTime;
    }

    public long getQpSolveTime() {
        return qpSolveTime;
    }

    public void setQpSolveTime(long qpSolveTime) {
        this.qpSolveTime = qpSolveTime;
    }

    public int getNumberOfLPs() {
        return numberOfLPs;
    }

    public void setNumberOfLPs(int numberOfLPs) {
        this.numberOfLPs = numberOfLPs;
    }

    public int getNumberOfQPs() {
        return numberOfQPs;
    }

    public void setNumberOfQPs(int numberOfQPs) {
        this.numberOfQPs = numberOfQPs;
    }

    public int getNumberOfMIPs() {
        return numberOfMIPs;
    }

    public void setNumberOfMIPs(int numberOfMIPs) {
        this.numberOfMIPs = numberOfMIPs;
    }

    public int getConstraintsGenerated() {
        return constraintsGenerated;
    }

    public void setConstraintsGenerated(int constraintsGenerated) {
        this.constraintsGenerated = constraintsGenerated;
    }

    public int getIgnoredConstraints() {
        return ignoredConstraints;
    }

    public void setIgnoredConstraints(int ignoredConstraints) {
        this.ignoredConstraints = ignoredConstraints;
    }

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

    @Override
    public String toString() {
        return "MetaInfo[constraintsGenerated=" + constraintsGenerated + ", ignoredConstraints=" + ignoredConstraints + ", numberOfMIPs=" + numberOfMIPs + ", numberOfQPs="
                + numberOfQPs + ", numberOfLPs=" + numberOfLPs + ", numberOfApprox=" + numberOfApproximations + ", mipSolveTime=" + mipSolveTime + " , qpSolveTime=" + qpSolveTime
                + " , lpSolveTime=" + lpSolveTime + ", javaRunTime=" + javaRuntime + ", approxSolveTime=" + approxSolveTime + "]";
    }
}
