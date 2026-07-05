package u04.typeclasses

object HigherKindedTypesWithExtensionMethods:

  // 0-kinded types, that is, standard types
  trait FactorialModuleType:
    extension (n: Int) def factorial(): Int
  object BasicFactorialModule extends FactorialModuleType:
    extension (n: Int) def factorial(): Int = if n == 0 then 1 else n * (n-1).factorial()
  import BasicFactorialModule.* 

  val f = 5.factorial()

  // 1-kinded types, that is, types generic over a 0-kinded type
  trait Showable[T]:
    extension (t: T) def show(): String
  object ShowableInt extends Showable[Int]:
    extension (i: Int) def show(): String = "" + i
  import ShowableInt.*

  val s = 5.show()

  // 2-kinded types, that is, types generic over a 1-kinded type
  trait Filterable[T[_]]:
    extension [A](t: T[A]) def filter(f: A => Boolean): T[A]
  import u04.datastructures.Optionals.*, Optional.*
  object FilterableOptional extends Filterable[Optional]:
    extension [A](t: Optional[A]) def filter(f: A => Boolean): Optional[A] = t match
      case Just(a) if f(a) => Just(a)
      case _ => None()
  import FilterableOptional.*

  val opt = Just(11).filter(_ > 10)  