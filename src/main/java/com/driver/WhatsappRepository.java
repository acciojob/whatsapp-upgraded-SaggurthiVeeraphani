package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class WhatsappRepository {
    private Map<Group, List<User>> groupUserMap;
    private Map<Group, List<Message>> groupMessageMap;
    private Map<Message, User> senderMap;
    private Map<Group, User> adminMap;
    private Map<String, User> userMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMap = new HashMap<String, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    // 1)
    public String createUser(String name, String mobile) throws Exception
    {
        //If the mobile number exists in database, throw "User already exists" exception
        //Otherwise, create the user and return "SUCCESS"
        if (!userMobile.contains(mobile))
        {
            userMobile.add(mobile);
            User user = new User(name, mobile);
            userMap.put(name, user);
            return "SUCCESS";
        }
        else
        {
            throw new Exception("User already exists");
        }
    }

    // 2)
    public Group createGroup(List<User> users)
    {
        //int count = 0;
        if (users.size()>2)
        {
            customGroupCount++;
            String gName = "Group "+String.valueOf(customGroupCount);
            String adName = users.get(0).getName();
            Group grp = new Group(gName, users.size());
            groupUserMap.put(grp, users);
            adminMap.put(grp, users.get(0));
            return grp;
        }
        else if (users.size()==2)
        {
            String gName = users.get(1).getName();
            String adName = users.get(0).getName();
            Group grp = new Group(gName, users.size());
            groupUserMap.put(grp, users);
            adminMap.put(grp, users.get(0));
            return grp;
        }
        return null;         //if users.size()<2
    }

    // 3)
    public int createMessage(String content)
    {
        messageId++;
        Message message = new Message(messageId, content);
        return  messageId;
    }

    //4)
    public int sendMessage(Message message, User sender, Group group) throws Exception
    {
        if (groupUserMap.containsKey(group))
        {
            List<User> currUsers = groupUserMap.get(group);
            if (currUsers.contains(sender))
            {
                senderMap.put(message, sender);
                if (groupMessageMap.containsKey(group))
                {
                    List<Message> msgList = groupMessageMap.get(group);
                    msgList.add(message);
                    groupMessageMap.put(group, msgList);
                    return msgList.size();
                }
                else
                {
                    List<Message> msgList = new ArrayList<>();
                    msgList.add(message);
                    groupMessageMap.put(group, msgList);
                    return msgList.size();
                }
            }
            else
            {
                throw new Exception("You are not allowed to send message");
            }
        }
        else
        {
            throw new Exception("Group does not exist");
        }
    }

    // 5)
    public String changeAdmin(User approver, User user, Group group) throws Exception
    {
        if (adminMap.containsKey(group))
        {
            User currAdmin = adminMap.get(group);
            if (approver.equals(currAdmin))
            {
                List<User> currUsers = groupUserMap.get(group);
                if (currUsers.contains(user))
                {
                    adminMap.put(group, user);
                    return "SUCCESS";
                }
                else
                {
                    throw new Exception("User is not a participant");
                }
            }
            else
            {
                throw new Exception("Approver does not have rights");
            }
        }
        else
        {
            throw new Exception("Group does not exist");
        }
    }
    public int removeUserfromDb(User user)throws Exception{
        Group checkGroup = null;
        boolean userAvailability = false;
        for(Group group:groupUserMap.keySet()){
            List<User> userList = groupUserMap.get(group);
            for(User user1:userList){
                if(user1 == user){
                    checkGroup = group;
                    userAvailability = true;
                }
            }
        }
        if(!userAvailability){
            throw new Exception("User not found");
        }
        boolean adminCheck = false;
        for(Group group : adminMap.keySet()){
            if(adminMap.get(group) == user){
                adminCheck = true;
            }
        }
        if(adminCheck){
            throw new Exception("Cannot remove admin");
        }
        List<User> users = groupUserMap.get(checkGroup);
        for(User user1:users){
            if(user1 == user){
                users.remove(user1);
            }
        }
        Set<String> username = userMap.keySet();
        for(String s:username){
            if(userMap.get(s)==user){
                userMap.remove(s);
            }
        }
        List<Message> messages = groupMessageMap.get(checkGroup);
        for(Message message:messages){
            if(senderMap.get(message)==user){
                senderMap.remove(message);
                messages.remove(message);
            }
        }
        List<Message> messages1 = groupMessageMap.get(checkGroup);
        List<User> users1 = groupUserMap.get(checkGroup);

        int ans =messages1.size()+ users1.size()+messageId;


     return ans;

    }


}
