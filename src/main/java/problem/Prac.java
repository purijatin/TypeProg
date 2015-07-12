package problem;

/**
 *
 * Created by jatin on 28/6/15.
 */
public class Prac {
}

class Animal<T extends Food>{
 public void eat(T food){

 }
}

class Cow extends Animal<Grass>{
    @Override
    public void eat(Grass food){

    }
}

interface Food{

}

class Grass implements Food{

}
