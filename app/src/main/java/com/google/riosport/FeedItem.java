package com.google.riosport;

import android.graphics.drawable.Drawable;

/**
 * Created by pierre-alexandremaury on 15/11/2014.
 */
public class FeedItem {
        private int id, sportPic;
        private String name, status, timeStamp;

        public FeedItem() {
        }

        public FeedItem(int id, String name, String status,
                        int sportPic, String timeStamp) {
            super();
            this.id = id;
            this.name = name;
            this.status = status;
            this.sportPic = sportPic;
            this.timeStamp = timeStamp;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getSportPic() {
            return sportPic;
        }

        public void setSportPic(int sportPic) {
            this.sportPic = sportPic;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }
    }

