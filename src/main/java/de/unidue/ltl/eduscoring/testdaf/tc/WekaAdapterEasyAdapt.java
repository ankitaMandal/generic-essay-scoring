package de.unidue.ltl.eduscoring.testdaf.tc;



import org.dkpro.lab.task.impl.ExecutableTaskBase;
import org.dkpro.tc.core.ml.TcShallowLearningAdapter;
import org.dkpro.tc.ml.weka.WekaAdapter;



public class WekaAdapterEasyAdapt extends WekaAdapter{

	   public static TcShallowLearningAdapter getInstance()
	    {
	        return new WekaAdapterEasyAdapt();
	    }

	    @Override
	    public ExecutableTaskBase getTestTask()
	    {
	        return new WekaTestTask_easyAdapt();
	    }

}
