package expression.operations;

public interface Operation<T> {

    T add(T a, T b);

    T subtract(T a, T b);

    T multiply(T a, T b);

    T divide(T a, T b);

    T negate(T a);

    T pow(T a, T b);

    T log(T a, T b);

    T sin(T a);

    T cos(T a);

    T toConst(String string);

    T convertTo(double x);

    double convertFrom(T x);
}
