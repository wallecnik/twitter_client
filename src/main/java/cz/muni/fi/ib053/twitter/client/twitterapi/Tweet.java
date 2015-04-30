package cz.muni.fi.ib053.twitter.client.twitterapi;

/**
 * Created by Wallecnik on 23.04.15.
 * @author Dužinka
 * @version 29.04.2015
 * Warning! Instead of Instant created, String is used temporarily.
 * There might be some difficulties with String -> Instant conversion.
 */
public final class Tweet implements Comparable<Tweet> {

    //private final Instant   created;      // TODO: Solve problems with String -> Instant conversion
    private final Long      id;
    private final String    username;
    private final String    text;
    private final String    created;

    public Tweet(Long id, String created, String username, String text) {
        this.id      = id;
        this.created = created;
        this.username = username;
        this.text = text;
    }

    public String getCreated() {
        return created;
    }

    public String getText() {
        return text;
    }

    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Tweet o) {
        return this.created.compareTo(o.created);
    }

    @Override
    public String toString() {
        return
                "User: " + username + '\n' +
                "Created: " + created + '\n' +
                text;
    }
}