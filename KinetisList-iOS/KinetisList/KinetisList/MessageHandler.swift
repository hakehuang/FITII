//
//  MessageHandler.swift
//  KinetisList
//
//  Created by Jingyi Gao on 15/3/30.
//  Copyright (c) 2015年 FSL. All rights reserved.
//

import Foundation
class MessageOperation{
    class Task {
        var msg:String = ""
        var block:Bool = true
        var objArg1:AnyObject
        var objArg2:AnyObject
        var objArg3:AnyObject
        
        init(message:String, block:Bool,obj1:AnyObject,obj2:AnyObject,obj3:AnyObject){
            self.msg = message
            self.block = block
            self.objArg1 = obj1
            self.objArg2 = obj2
            self.objArg3 = obj3
            
        }
            
    }
    lazy var downloadInProgress = [NSIndexPath:NSOperation]()
    lazy var taskQueue:NSOperationQueue = {
        var queue = NSOperationQueue()
        queue.name = "task queue"
        queue.maxConcurrentOperationCount = 1
        return queue
    }()
    
}