package com.migu.schedule;


import com.migu.schedule.constants.ReturnCodeKeys;
import com.migu.schedule.info.Task;
import com.migu.schedule.info.TaskInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
*类名和方法不能修改
 */
public class Schedule {

	//注册中心
	private Set<Integer> set=new HashSet<Integer>();
	
	//任务在服务节点运行(节点ID捆绑任务ID)
	private List<TaskInfo> infos=new ArrayList<TaskInfo>();
	
	//任务队列
	 private BlockingQueue<Task> queue = new LinkedBlockingQueue<Task>(10000);
	 
	//统计各节点的任务量和消耗率
	Map<Integer,Integer> map=new HashMap<Integer,Integer>();
	
    public int init() {
        infos.clear();
        queue.clear();
        set.clear();
        return ReturnCodeKeys.E001;
    }


    public int registerNode(int nodeId) {
        //注册节点
    	if(nodeId<=0) {
    		return ReturnCodeKeys.E004;
    	}else if(set.contains(nodeId)) {
    		return ReturnCodeKeys.E005;
    	}else {
    		set.add(nodeId);
    		return ReturnCodeKeys.E003;
    	}
    }

    public int unregisterNode(int nodeId) {
    	if(nodeId<=0) {
    		return ReturnCodeKeys.E004;
    	}else if(!set.contains(nodeId)) {
    		return ReturnCodeKeys.E007;
    	}else {
    		for(TaskInfo info:infos) {
    			if(info.getNodeId()==nodeId&&info.getTaskId()!=0) {
    				//将任务挂起到队列中
    				Task task=new Task();
    				task.setTaskId(info.getTaskId());
    				queue.offer(task);
    				//在注册中心注销
    				set.remove(nodeId);
    			}
    		}
    		return ReturnCodeKeys.E006;
    	}
    }


    public int addTask(int taskId, int consumption) {
    	if(taskId<=0) {
    		return ReturnCodeKeys.E009;
    	}
    	for(int i=0;i<queue.size();i++) {
    		Task task=queue.poll();
    		if(task.getTaskId()==taskId) {
    			return ReturnCodeKeys.E010;
    		}
    	}
    	//添加任务
    	Task task=new Task();
    	task.setTaskId(taskId);
    	queue.offer(task);
    	return ReturnCodeKeys.E008;
    }


    public int deleteTask(int taskId) {
    	if(taskId<=0) {
    		return ReturnCodeKeys.E009;
    	}
    	int taskIdisExist=0;
    	for(int i=0;i<queue.size();i++) {
    		Task task=queue.poll();
    		if(task.getTaskId()==taskId) {
    			taskIdisExist++;
    			//删除任务
    			queue.remove(task);
    		}
    	}
    	if(taskIdisExist==0) {
    		return ReturnCodeKeys.E012;
    	}
    	
    	return ReturnCodeKeys.E011;
    }


    public int scheduleTask(int threshold) {
        if(!queue.isEmpty()) {
        	//先计算一下目前各个节点任务量和消耗率
        	for(TaskInfo info:infos) {
    			int nodeId=info.getNodeId(); 
    			int taskId=info.getTaskId();
    			for(int i=0;i<queue.size();i++) {
    	    		Task task=queue.poll();
    	    		if(task.getTaskId()==taskId) {
    	    			int consumption=task.getConsumption();
    	    		}
    	    	}
    	    }
        }
        return ReturnCodeKeys.E000;
    }


    public int queryTaskStatus(List<TaskInfo> tasks) {
        // TODO 方法未实现
        return ReturnCodeKeys.E000;
    }

}
