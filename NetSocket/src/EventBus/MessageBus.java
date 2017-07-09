/**
 * 
 */
package EventBus;

import java.util.concurrent.ConcurrentHashMap;

import com.google.common.eventbus.EventBus;

/**
 * @author jinyu
 *
 */
public class MessageBus {
 static    ConcurrentHashMap<String,EventBus> hashMap=new ConcurrentHashMap<String,EventBus>();
 public  static EventBus getBus(String identifier)
 {
     EventBus  eventBus=  hashMap.getOrDefault(identifier, null);
     if(eventBus==null)
     {
         eventBus=new EventBus(identifier);
         hashMap.put(identifier, eventBus);
     }
    return eventBus; 
 }
 public static void  register(String identifier,Object obj)
 {
     getBus(identifier).register(obj);
 }
 public static void  unregister(String identifier,Object obj)
 {
     getBus(identifier).unregister(obj);
 }
 public static void  post(String identifier,Object msg)
 {
     getBus(identifier).post(msg);
 }
 public static void  register(Object obj)
 {
     getBus("msg").register(obj);
 }
 public static void  unregister(Object obj)
 {
     getBus("msg").unregister(obj);
 }
 public static void  post(Object msg)
 {
     getBus("msg").post(msg);
 }
}
