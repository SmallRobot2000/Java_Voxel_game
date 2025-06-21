package io.github.myPackage;

public class Vector2i {
    public int x;
    public int y;

    // Constructors
    public Vector2i() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Copy constructor
    public Vector2i(Vector2i other) {
        this.x = other.x;
        this.y = other.y;
    }

    // Add another vector
    public Vector2i add(Vector2i other) {
        return new Vector2i(this.x + other.x, this.y + other.y);
    }

    // Subtract another vector
    public Vector2i subtract(Vector2i other) {
        return new Vector2i(this.x - other.x, this.y - other.y);
    }

    // Multiply by scalar
    public Vector2i multiply(int scalar) {
        return new Vector2i(this.x * scalar, this.y * scalar);
    }

    // Dot product
    public int dot(Vector2i other) {
        return this.x * other.x + this.y * other.y;
    }

    // Equals and hashCode for correct comparisons and use in collections
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector2i vector2i = (Vector2i) obj;
        return x == vector2i.x && y == vector2i.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "Vector2i(" + x + ", " + y + ")";
    }
}
