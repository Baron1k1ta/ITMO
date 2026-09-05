package info.kgeorgiy.ja.baronov.student;

import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StudentDB implements StudentQuery {

    final private static Comparator<Student> STUDENT_COMPARATOR =
            Comparator
                    .comparing(Student::firstName)
                    .thenComparing(Student::lastName)
                    .thenComparing(Student::id);


    private static <T> List<T> getStudentList(List<Student> students, Function<Student, T> mapper) {
        return students.stream().map(mapper).collect(Collectors.toList());
    }

//    private static <T, R extends Collection<T>> R getStudentCollection(
//            List<Student> students,
//            Function<Student, T> mapper,
//            Collector<T, ?, R> collector) {
//        return students.stream()
//                .map(mapper)
//                .collect(collector);
//    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getStudentList(students, Student::firstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getStudentList(students, Student::lastName);
    }


    @Override
    public List<GroupName> getGroupNames(List<Student> students) {
        return getStudentList(students, Student::groupName);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return getStudentList(students, student -> student.firstName() + " " + student.lastName());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return students.stream().map(Student::firstName).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream()
                .max(Comparator.comparingInt(Student::id))
                .map(Student::firstName)
                .orElse("");
    }

    private static List<Student> sortStudentList(Collection<Student> students, Comparator<? super Student> comparator) {
        return students.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sortStudentList(students, Comparator.comparingInt(Student::id));
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sortStudentList(students, STUDENT_COMPARATOR);
    }

    private static List<Student> findStudentByParametr(Collection<Student> students, Predicate<Student> predicate) {
        return students.stream().filter(predicate).sorted(STUDENT_COMPARATOR).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return findStudentByParametr(students, student -> student.firstName().equals(name));
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return findStudentByParametr(students, student -> student.lastName().equals(name));
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return findStudentByParametr(students, student -> student.groupName().equals(group));
    }


    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return students.stream()
                .filter(student -> student.groupName().equals(group))
                .collect(Collectors
                        .toMap(Student::lastName, Student::firstName, BinaryOperator.minBy(String::compareTo)));
    }
}
