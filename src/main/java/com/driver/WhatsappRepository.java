package com.driver;

import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;

@Repository
public class WhatsappRepository {
    int groupCount = 0;
    int msgCount = 0;

    //Key --> mobile
    HashMap<String,User> userHashMap=new HashMap<>();

    //Key --> Group
    HashMap<Group,List<User>> groupHashMap=new HashMap<>();

    HashMap<Group,List<Message>> messagesInGroup=new HashMap<>();
    HashMap<User,List<Message>> userMessageList=new HashMap<>();


    List<Message> messageList=new ArrayList<>();


    public String createUser(String name, String mobile) throws Exception{
        if(userHashMap.containsKey(mobile)){
            throw new Exception("User already exists");
        }
        User user = new User(name,mobile);
        userHashMap.put(mobile,user);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        if(users.size()==2){
            Group group = new Group(users.get(1).getName(),2);
            groupHashMap.put(group,users);
            return group;
        }
        Group group=new Group("Group "+ ++groupCount,users.size());
        groupHashMap.put(group,users);
        return group;
    }

    public int createMessage(String content){
        Message msg = new Message(++msgCount,content);
        messageList.add(msg);
        msg.setTimestamp(new Date());
        return msgCount;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        if(!groupHashMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }

        boolean userCheck = false;
        for(User user:groupHashMap.get(group)){
            if(user.equals(sender)){
                userCheck = true;
                break;
            }
        }

        if(!userCheck){
            throw new Exception("You are not allowed to send message");
        }

        if(messagesInGroup.containsKey(group)){
            messagesInGroup.get(group).add(message);
        }
        else {
            List<Message> messages=new ArrayList<>();
            messages.add(message);
            messagesInGroup.put(group,messages);
        }

        if(userMessageList.containsKey(sender)){
            userMessageList.get(sender).add(message);
        }
        else {
            List<Message> messages=new ArrayList<>();
            messages.add(message);
            userMessageList.put(sender,messages);
        }

        return messagesInGroup.get(group).size();
    }


    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!groupHashMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }

        User admin = groupHashMap.get(group).get(0);
        if(!approver.equals(admin)){
            throw new Exception("Approver does not have rights");
        }

        boolean userCheck = false;
        for(User user1 :groupHashMap.get(group)){
            if(user1.equals(user)){
                userCheck = true;
                break;
            }
        }

        if(!userCheck){
            throw new Exception("User is not a participant");
        }

        User newAdmin = null;
        Iterator<User> userIterator=groupHashMap.get(group).iterator();

        while(userIterator.hasNext()){
            User u = userIterator.next();
            if(u.equals(user)){
                newAdmin = u;
                userIterator.remove();
            }
        }

        groupHashMap.get(group).add(0,newAdmin);

        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception{
        boolean checkUser = false;
        Group group = null;
        for(Group group1 : groupHashMap.keySet()){
            for(User user1 : groupHashMap.get(group1)){
                if(user1.equals(user)){
                    checkUser = true;
                    group = group1;
                    break;
                }
            }
        }
        if(!checkUser){
            throw new Exception("User not found");
        }

        if(groupHashMap.get(group).get(0).equals(user)){
            throw new Exception("Cannot remove admin");
        }


        List<Message> userMessages=userMessageList.get(user);
        List<Message> updatedMsgsIngroup = new ArrayList<>();

        for(Message message:messagesInGroup.get(group)){
            if(userMessages.contains(message)){
                continue;
            }
            updatedMsgsIngroup.add(message);
        }

        messagesInGroup.put(group,updatedMsgsIngroup);


        List<Message> updatedMsgsInList = new ArrayList<>();
        for(List<Message> messages : messagesInGroup.values()){
            for(Message message : messages){
                updatedMsgsInList.add(message);
            }
        }

        messageList = updatedMsgsInList;

        groupHashMap.get(group).remove(user);

        userMessageList.remove(user);

        return groupHashMap.get(group).size() + messagesInGroup.get(group).size() + messageList.size();
    }

    public String findMessage(Date start, Date end, int k) {
        return null;
    }
}
