package multimethods

trait Animal:
    def animalType: String
    def name: String

case class MultiMethod[I, O, D] private(dispatch: I => D, methods: Map[D, I => O] = Map(), default: Option[I => O]) extends Function[I, O]:
    private def addDefault(method: I => O): MultiMethod[I, O, D] =
        copy(default = Some(method))

    private def addMethod(d: D, method: I => O): MultiMethod[I, O, D] =
        copy(methods = methods + (d -> method))

    override def apply(i: I): O =
        methods.get(dispatch(i)).orElse(default).map(m => m(i)).get

object MultiMethod:
    def multi[I, O, D](dispatch: I => D): MultiMethod[I, O, D] =
        new MultiMethod[I, O, D](dispatch, Map(), None)

    def method[I, O, D](d: D, m: I => O)(multi: MultiMethod[I, O, D]): MultiMethod[I, O, D] =
        multi.addMethod(d, m)

    def method[I, O, D](m: I => O)(multi: MultiMethod[I, O, D]): MultiMethod[I, O, D] =
        multi.addDefault(m)

object MultiTest:
    import MultiMethod._

    def greetDispatch(animal: Animal): String = animal.animalType

    def greetDog(animal: Animal): String = s"Woof my name is ${animal.name}"
    def greetCat(animal: Animal): String = s"Meow I am ${animal.name}"
    def greetCow(animal: Animal): String = s"Moo my name is ${animal.name}"
    def greetDefault(animal: Animal): String = s"My name is ${animal.name}"

    var greet: MultiMethod[Animal, String, String] = multi[Animal, String, String](greetDispatch)
    greet = method("dog", greetDog)(greet)
    greet = method("cat", greetCat)(greet)
    greet = method("cow", greetCow)(greet)
    greet = method(greetDefault)(greet)

    val myDog = new Animal:
        override def animalType: String = "dog"
        override def name: String = "Fido"
    val myCat = new Animal:
        override def animalType: String = "cat"
        override def name: String = "Milo"
    val myCow = new Animal:
        override def animalType: String = "cow"
        override def name: String = "Clarabelle"

    val myHorse = new Animal:
        override def animalType: String = "horse"
        override def name: String = "Horace"

    @main
    def run(): Unit =
        println(greet(myDog))
        println(greet(myCat))
        println(greet(myCow))
        println(greet(myHorse))
