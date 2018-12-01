package server.model;

import common.MsgContainerDTO;

import java.util.ArrayList;
import java.util.LinkedList;

public class MsgContainer implements MsgContainerDTO {

        private LinkedList<String> message = new LinkedList<>();
        private String status;

        public MsgContainer(ArrayList<String> data, String status){
            this.status = status;
            if(data != null){
                extractData(data);
            }
        }

        private void extractData(ArrayList<String> data){
            for(int i = 0; i < data.size();  i++){
                message.addLast(data.get(i));
            }
            data.clear();
        }

        public String getStatus(){
            return status;
        }

        public LinkedList<String> getMsg(){
            return message;
        }



}
