package u04.typeclasses

object HigherKindedTypes:

  // 0-kinded types, that is, standard types
  trait FactorialModuleType:
    def factorial(n: Int): Int

  object BasicFactorialModule extends FactorialModuleType:
    def factorial(n: Int): Int = if n == 0 then 1 else n * factorial(n-1)

  val f = BasicFactorialModule.factorial(5) 

  // 1-kinded types, that is, types generic over a 0-kinded type
  trait Showable[T]:
    def show(t: T): String

  object ShowableInt extends Showable[Int]:
    def show(i: Int): String = "" + i

  val s = ShowableInt.show(5)

  // 2-kinded types, that is, types generic over a 1-kinded type
  trait Filterable[T[_]]:
    def filter[A](t: T[A])(f: A => Boolean): T[A]

  import u04.datastructures.Optionals.*, Optional.*
  object FilterableOptional extends Filterable[Optional]:
    def filter[A](t: Optional[A])(f: A => Boolean): Optional[A] = t match
      case Just(a) if f(a) => Just(a)
      case _ => None()

  val opt = FilterableOptional.filter(Just(11))(_ > 10)  