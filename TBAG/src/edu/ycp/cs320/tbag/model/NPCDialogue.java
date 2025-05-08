package edu.ycp.cs320.tbag.model;

public class NPCDialogue {
    private int dialogueId;
    private String dialogueText;
    private String response1;
    private String response2;
    private String response3;
    private int next1;
    private int next2;
    private int next3;

    public NPCDialogue(int dialogueId, String dialogueText, String response1, String response2, String response3, int next1, int next2, int next3) {
        this.dialogueId = dialogueId;
        this.dialogueText = dialogueText;
        this.response1 = response1;
        this.response2 = response2;
        this.response3 = response3;
        this.next1 = next1;
        this.next2 = next2;
        this.next3 = next3;
    }

    public int getDialogueId() { return dialogueId; }
    public String getDialogueText() { return dialogueText; }
    public String getResponse1() { return response1; }
    public String getResponse2() { return response2; }
    public String getResponse3() { return response3; }
    public int getNext1() { return next1; }
    public int getNext2() { return next2; }
    public int getNext3() { return next3; }
}