package com.epam.dojo.notifier.contest;

import lombok.Data;

import java.util.*;

@Data
public class Contest {

    private String contestId;
    private String title;
    private String slackToken;
    private String slackChannel;
    private String senseiEmails;

    private Map<EventType, Set<NotifierType>> notifiers;

    public Contest() {
        notifiers = new HashMap<>();
        notifiers.put(EventType.ANY_LEADERBOARD_CHANGE,
                new HashSet<>(Arrays.asList(NotifierType.SLACK)));
        /*notifiers.put(EventType.PARTICIPANT_SCORE_CHANGE,
                new HashSet<>(Arrays.asList(NotifierType.SLACK)));*/
    }

    public boolean getCommonSlack() {
        if (notifiers.containsKey(EventType.ANY_LEADERBOARD_CHANGE)) {
            return notifiers.get(EventType.ANY_LEADERBOARD_CHANGE).contains(NotifierType.SLACK);
        }
        return false;
    }

    public void setCommonSlack(boolean commonSlack) {
        Set<NotifierType> nt = notifiers.getOrDefault(EventType.ANY_LEADERBOARD_CHANGE, new HashSet<>());
        if (commonSlack) nt.add(NotifierType.SLACK);
        else nt.remove(NotifierType.SLACK);
    }

    public boolean getCommonMail() {
        if (notifiers.containsKey(EventType.ANY_LEADERBOARD_CHANGE)) {
            return notifiers.get(EventType.ANY_LEADERBOARD_CHANGE).contains(NotifierType.EMAIL);
        }
        return false;
    }

    public void setCommonMail(boolean commonMail) {
        Set<NotifierType> nt = notifiers.getOrDefault(EventType.ANY_LEADERBOARD_CHANGE, new HashSet<>());
        if (commonMail) nt.add(NotifierType.EMAIL);
        else nt.remove(NotifierType.EMAIL);
    }

    public boolean getPersonalSlack() {
        if (notifiers.containsKey(EventType.PARTICIPANT_SCORE_CHANGE)) {
            return notifiers.get(EventType.PARTICIPANT_SCORE_CHANGE).contains(NotifierType.SLACK);
        }
        return false;
    }

    public void setPersonalSlack(boolean personalSlack) {
        Set<NotifierType> nt = notifiers.getOrDefault(EventType.PARTICIPANT_SCORE_CHANGE, new HashSet<>());
        if (personalSlack) nt.add(NotifierType.SLACK);
        else nt.remove(NotifierType.SLACK);
    }

    public boolean getPersonalMail() {
        if (notifiers.containsKey(EventType.PARTICIPANT_SCORE_CHANGE)) {
            return notifiers.get(EventType.PARTICIPANT_SCORE_CHANGE).contains(NotifierType.EMAIL);
        }
        return false;
    }

    public void setPersonalMail(boolean personalMail) {
        Set<NotifierType> nt = notifiers.getOrDefault(EventType.PARTICIPANT_SCORE_CHANGE, new HashSet<>());
        if (personalMail) nt.add(NotifierType.EMAIL);
        else nt.remove(NotifierType.EMAIL);
    }
}
