
public class review {
    private String reviewerName;
    private int rating; // rating out of 5
    private String comment;

    public review(String reviewerName, int rating, String comment) {
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = comment;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "Reviewer: " + reviewerName + "\nRating: " + rating + "/5\nComment: " + comment;
    }
}
