
package change

import java.util.PriorityQueue

/* The wallet data structure holds a collection of bills */
/* NOTE: This is separate from the Register class so that Wallets can be passed as parameters to the Register */
data class Wallet(val twenties: Int = 0,
                    val tens: Int = 0,
                    val fives: Int = 0,
                    val twos: Int = 0,
                    val ones: Int = 0) {

    /* The total amount of money in the wallet */
    val total = (twenties * 20) + (tens * 10) + (fives * 5) + (twos * 2) + ones

    override fun toString(): String {

        return "$$total $twenties $tens $fives $twos $ones"
    }

    /* Return a new wallet that contains the contents of this wallet plus the contents of another wallet */
    fun add(rvalue: Wallet): Wallet {
        return Wallet(twenties + rvalue.twenties,
                        tens + rvalue.tens,
                        fives + rvalue.fives,
                        twos + rvalue.twos,
                        ones + rvalue.ones)
    }

    /* Return a new wallet that contains the contents of this wallet minus the contents of another wallet.  Throw an error if the subtraction would leave the new wallet with a negative number of bills of any denomination */
    fun subtract(rvalue: Wallet): Wallet {
        val new_wallet = Wallet(twenties - rvalue.twenties,
                        tens - rvalue.tens,
                        fives - rvalue.fives,
                        twos - rvalue.twos,
                        ones - rvalue.ones)
        // TODO: Actually throw the excpetion
        return new_wallet
    }
}

/* The register class has a wallet (the register drawer and supports operations on that wallet */
class Register (var drawer: Wallet) {

    /* Show the state of the register drawer */
    fun show(): String {
        return drawer.toString()
    }

    /* Add money to the drawer */
    fun put(input: Wallet) {
        println("Adding $input")
        drawer = drawer.add(input)
    }

    /* Remove money from the drawer */
    fun take(output: Wallet) {
        println("Removing $output")
        drawer = drawer.subtract(output)
    }

    /* Make change, if possible and remove the money from the drawer.

    With the denominations that we're working with, a greedy approach where
    pick the largest possible bill won't work.  (Ex. $6 can be made as 5+1 or
    as 2+2+2 and if there's no one to add to the five, then we would say that
    we can't make change when we might actually be able to.  Using a breadth
    first search would gaurntee that we'll get a correct answer, but that could end up checking a lot of possibilities that aren't usefull.  To get the best performance, I've implemented an A* search. */
    
    fun change(amount: Int) {
        println("Trying to make change for $amount")

        //Check to see if we're trying to make change for more than what is in the drawer.
        if (amount > drawer.total) {
            println("sorry")
            return
        }

        val nodes: MutableSet<Wallet> = mutableSetOf()
        val node_queue = PriorityQueue<SearchPosition>(listOf(
            SearchPosition(amount, Wallet(), drawer)
        ))

        while (node_queue.any()) {
            val current_node = node_queue.remove()
            println(current_node.amount.toString() + current_node.change)

            //Can we add a $20 bill to the change
            if (current_node.amount >= 20 && current_node.drawer.twenties >= 1) {

                val new_amount = current_node.amount - 20
                val new_change = current_node.change.copy(twenties = current_node.change.twenties + 1)
                val new_drawer = current_node.drawer.copy(twenties = current_node.drawer.twenties - 1)

                val new_node = SearchPosition(new_amount, new_change, new_drawer)

                //Check to see if we've made exact change
                if (new_amount == 0) {
                    take(new_change)
                    return
                }

                //Check to see if the new node is already in the set of nodes that we will check.  If not, then add it to both the set and the priority queue.
                if (! nodes.contains(new_change)) {
                    nodes.add(new_change)
                    node_queue.add(new_node)
                }
            }

            //Can we add a $10 bill to the change
            if (current_node.amount >= 10 && current_node.drawer.tens >= 1) {
                val new_amount = current_node.amount - 10
                val new_change = current_node.change.copy(tens = current_node.change.tens + 1)
                val new_drawer = current_node.drawer.copy(tens = current_node.drawer.tens - 1)

                val new_node = SearchPosition(new_amount, new_change, new_drawer)

                //Check to see if we've made exact change
                if (new_amount == 0) {
                    take(new_change)
                    return
                }

                //Check to see if the new node is already in the set of nodes that we will check.  If not, then add it to both the set and the priority queue.
                if (! nodes.contains(new_change)) {
                    nodes.add(new_change)
                    node_queue.add(new_node)
                }
            }

            //Can we add a $5 bill to the change
            if (current_node.amount >= 5 && current_node.drawer.fives >= 1) {
                val new_amount = current_node.amount - 5
                val new_change = current_node.change.copy(fives = current_node.change.fives + 1)
                val new_drawer = current_node.drawer.copy(fives = current_node.drawer.fives - 1)

                val new_node = SearchPosition(new_amount, new_change, new_drawer)

                //Check to see if we've made exact change
                if (new_amount == 0) {
                    take(new_change)
                    return
                }

                //Check to see if the new node is already in the set of nodes that we will check.  If not, then add it to both the set and the priority queue.
                if (new_change !in nodes) {
                    nodes.add(new_change)
                    node_queue.add(new_node)
                }
            }

            //Can we add a $2 bill to the change
            if (current_node.amount >= 2 && current_node.drawer.twos >= 1) {
                val new_amount = current_node.amount - 2
                val new_change = current_node.change.copy(twos = current_node.change.twos + 1)
                val new_drawer = current_node.drawer.copy(twos = current_node.drawer.twos - 1)

                val new_node = SearchPosition(new_amount, new_change, new_drawer)

                //Check to see if we've made exact change
                if (new_amount == 0) {
                    take(new_change)
                    return
                }

                //Check to see if the new node is already in the set of nodes that we will check.  If not, then add it to both the set and the priority queue.
                if (new_change !in nodes) {
                    nodes.add(new_change)
                    node_queue.add(new_node)
                }
            }

            //Can we add a $1 bill to the change
            if (current_node.amount >= 1 && current_node.drawer.ones >= 1) {
                val new_amount = current_node.amount - 1
                val new_change = current_node.change.copy(ones = current_node.change.ones + 1)
                val new_drawer = current_node.drawer.copy(ones = current_node.drawer.ones - 1)

                val new_node = SearchPosition(new_amount, new_change, new_drawer)

                //Check to see if we've made exact change
                if (new_amount == 0) {
                    take(new_change)
                    return
                }

                //Check to see if the new node is already in the set of nodes that we will check.  If not, then add it to both the set and the priority queue.
                if (new_change !in nodes) {
                    nodes.add(new_change)
                    node_queue.add(new_node)
                }
            }
        }
        
    }

    private class SearchPosition(val amount: Int,
                                 val change: Wallet,
                                 val drawer: Wallet) : Comparable<SearchPosition> {

        /* Compare two search positions. We do the comparision by first
            checking the heuristic that we're using for the A* search
            and then we break ties by checking the amount of change that still needs to be given. */
        override fun compareTo(other: SearchPosition): Int {
            val our_heuristic = heuristic()
            val other_heuristic = other.heuristic()
            
            when {
                our_heuristic < other_heuristic -> return -1
                our_heuristic > other_heuristic -> return 1
                amount < other.amount -> return -1
                amount > other.amount -> return 1
                else -> return 0
            }
        }

        /* The A* heuristic function returns the minimum number of bills that
            are needed to make the amount of change we still need to give */
        fun heuristic(n: Int = amount): Int = when (n) {
            1 -> 1 //$1
            2 -> 1 //$2
            3 -> 2 //$2 + $1
            4 -> 2 //$2 + $2
            5 -> 1 //$5
            6 -> 2 //$5 + $1
            7 -> 2 //$5 + $2
            8 -> 3 //$5 + $2 + $1
            9 -> 3 //$5 + $2 + $2
            10 -> 1 //$10
            11 -> 2 //$10 + $1
            12 -> 2 //$10 + $2
            13 -> 3 //$10 + $2 + $1
            14 -> 3 //$10 + $2 + $2
            15 -> 2 //$10 + $5
            16 -> 3 //$10 + $5 + $1
            17 -> 3 //$10 + $5 + $2
            18 -> 4 //$10 + $5 + $2 + $1
            19 -> 4 //$10 + $5 + $2 + $2
            20 -> 1 //$20
            else -> 1 + heuristic(n - 20) //$20 + ...
        }
    
    }
}

fun main(args: Array<String>) {
    println("ready")

    val myRegister = Register(Wallet(1, 2, 3, 4, 5))
    println(myRegister.show())

    myRegister.put(Wallet(1, 2, 3, 0, 5))
    println(myRegister.show())

    myRegister.take(Wallet(1, 4, 3, 0, 10))
    println(myRegister.show())

    myRegister.change(11)
    println(myRegister.show())

    myRegister.change(14)
    println(myRegister.show())

    //var input = readLine()
    //println(input)

/* TODO: Implement these

show - display contents
put - add bills
take - remove bills
change - make change, error is "sorry"
quit - exit
*/
}
