package ext.dtx;

public class TaskHistory {
   private String activityName = "N/A";
   private String assigne = "N/A";
   private String role = "N/A";
   private String comments = "N/A";
   private String vote = "N/A";
   private String actStatus = "N/A";
   private String completed = "N/A";
   private String start = "N/A";
   private String signature = "N/A";
   private String completedBy = "N/A";
   private String duration = "N/A";

   public TaskHistory(String activityName, String assigne, String role, String comments, String vote, String actStatus, String completed, String start, String signature, String completedBy, String duration) {
      this.activityName = activityName;
      this.assigne = assigne;
      this.role = role;
      this.comments = comments;
      this.vote = vote;
      this.actStatus = actStatus;
      this.completed = completed;
      this.start = start;
      this.signature = signature;
      this.completedBy = completedBy;
      this.duration = duration;
   }

   public TaskHistory() {
   }

   public String getduration() {
      return this.duration;
   }

   public String getActivityName() {
      return this.activityName;
   }

   public void setActivityName(String activityName) {
      this.activityName = activityName;
   }

   public String getAssigne() {
      return this.assigne;
   }

   public void setAssigne(String assigne) {
      this.assigne = assigne;
   }

   public String getRole() {
      return this.role;
   }

   public void setRole(String role) {
      this.role = role;
   }

   public String getComments() {
      return this.comments;
   }

   public void setComments(String comments) {
      this.comments = comments;
   }

   public String getVote() {
      return this.vote;
   }

   public void setVote(String vote) {
      this.vote = vote;
   }

   public String getActStatus() {
      return this.actStatus;
   }

   public void setActStatus(String actStatus) {
      this.actStatus = actStatus;
   }

   public String getCompleted() {
      return this.completed;
   }

   public void setCompleted(String completed) {
      this.completed = completed;
   }

   public String getStart() {
      return this.start;
   }

   public void setStart(String start) {
      this.start = start;
   }

   public String getSignature() {
      return this.signature;
   }

   public void setSignature(String signature) {
      this.signature = signature;
   }

   public void setDuraton(String duration) {
      this.duration = duration;
   }

   public String getCompletedBy() {
      return this.completedBy;
   }

   public void setCompletedBy(String completedBy) {
      this.completedBy = completedBy;
   }
}
