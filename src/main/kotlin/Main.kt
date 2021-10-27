import java.lang.Exception
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

var islive :Boolean = false
var name: String = ""
var pin: Int = 0
val df = DecimalFormat("#.##")

fun getCurrentDate():String{
    val dt = SimpleDateFormat("MMM/dd/yyyy hh:mm aa")
    return dt.format(Date())
}

data class Customer(var accnumber: Int, var pin: Int, var user: String, var name: String, var bal: Double, var tHistory: ArrayList<saveHistory>){

    fun recordStart(amount: Double,type: String) {
        this.tHistory.add(saveHistory(getCurrentDate(), amount, type))
    }
    fun getAccdata(): Any {
        return "accountNumber=$accnumber user=$user pin=$pin name=$name"
    }
}
open class saveHistory(var date: String = "", var amount: Double = 0.0, var type: String = ""){
    override fun toString(): String {
        return "date:$date, amount:$amount, type:$type"
    }
}

// main data to costumers
var customerList = listOf(
    Customer(112223,1234,"dan","daniel", 500.0,
        arrayListOf(
            saveHistory("Oct/25/2021 04:42 PM",4560.0,"Withdraw"),
            saveHistory("Oct/25/2021 05:23 PM",3320.0,"Deposit"),
            saveHistory("Oct/25/2021 06:34 PM",5332.0,"Withdraw"),
            saveHistory("Oct/25/2021 08:45 PM",4423.0,"PtoP"))
    ),
    Customer(334443,1221,"bob","bobot", 500.0,
        arrayListOf(
            saveHistory("Oct/25/2021 05:40 PM",5435.0,"Withdraw"),
            saveHistory("Oct/25/2021 05:55 PM",6754.0,"Withdraw"),
            saveHistory("Oct/25/2021 06:32 PM",7098.0,"Deposit"),
            saveHistory("Oct/25/2021 09:02 PM",7807.0,"PtoP"))
    ),
    Customer(444212,1212,"ted","teddy", 500.0,
        arrayListOf(
            saveHistory("Oct/25/2021 03:42 PM",6789.0,"Withdraw"),
            saveHistory("Oct/25/2021 05:02 PM",5858.0,"PtoP"),
            saveHistory("Oct/25/2021 06:59 PM",8064.0,"Withdraw"),
            saveHistory("Oct/25/2021 10:22 PM",4687.0,"Deposit"))
    ),
    Customer(322112,2323,"jan","janwel", 500.0,
        arrayListOf(
            saveHistory("Oct/25/2021 05:00 PM",9855.0,"PtoP"),
            saveHistory("Oct/25/2021 06:12 PM",5965.0,"Deposit"),
            saveHistory("Oct/25/2021 09:35 PM",4594.0,"Withdraw"),
            saveHistory("Oct/25/2021 10:52 PM",8068.0,"Withdraw"))
    )
)


fun List<Customer>.filterByAccn(accnumber: Int) = this.filter { it.accnumber == accnumber } //filter find Customer By Account number
fun List<Customer>.filterByUser(nameId: String) = this.filter { it.user == nameId } //filter find Customer By Account Name

fun main() {
    if (!islive) {
        println("Welcome to BDO\nSimple ATM")
        islive = true
        ATM().showAccs()
        ATM().userLogin()
    }else {
        ATM().userLogin()
    }
}

class ATM: Algorithm() {
    //main function
    fun userLogin() {
        try {
            if (name.isEmpty()) {
                print("Enter user: ")
                name = readLine().toString()
            }
            //check data user if existing
            val getd = customerList.filterByUser(name)
            val user = getd[0]
            if (name.isNotEmpty()) {
                dispName(user)
                print("Enter pin: ")
                pin = Integer.valueOf(readLine())
                //check pin if match to user
                val validPin: Customer = getPin(pin)
                if (user == validPin) {
                    display()
                    val select = Integer.valueOf(readLine())
                    operation(select, user)
                } else {
                    println("Wrong pin please try again")
                    name = ""
                    userLogin()
                }
            }
        } catch (e: Exception) {
            println("User does not exist")
            name = ""
            userLogin()
        }
    }

    // Operation functions
    private fun operation(ope: Any, user: Customer) {
        when (ope) {
            1 -> {
                withdraw(user)
                additional(user)
            }
            2 -> {
                deposit(user)
                additional(user)
            }
            3 -> {
                println("Your Balance is ${checkBal(user)} Petots")
                additional(user)
            }
            4 -> {
                saveHistory(user)
                additional(user)
            }
            5 -> {
                sendMoney(user)
                additional(user)
            }
            6 -> {
                println("Logout")
                pin = 0
                name = ""
                userLogin()
            }
            7 -> {
                println("System close in 2sec")
                Thread.sleep(2_000)
                print("bye")
                System.exit(0)
            }
            else -> {
                println("Invalid input!!")
                display()
                val select = Integer.valueOf(readLine())
                operation(select, user)
            }
        }
    }
}

open class Algorithm {
    //in addition, customer can choose to continue or exit
    fun additional(user: Customer) {
        println("Do you want another Operation? \nEnter 1: to continue. 2: to login again. 3: exit")
        when (readLine()?.toInt()) {
            1 -> {
                ATM().userLogin()
            }
            2 -> {
                println("Thank you for banking!")
                name = ""
                pin = 0
                ATM().userLogin()
            }
            3 -> {
                println("System Close in 2 sec")
                Thread.sleep(2_000)
                println("bye")
                System.exit(0)
            }
            else -> {
                println("Invalid input!!")
                additional(user)
            }
        }
    }

    //displays the features
    fun display() {
        println("Select operation you want to perform\n 1: Withdraw. 2: Deposit. 3: Balance. 4: Show history. 5: Send Money. 6: logout. 7: exit")
    }

    fun saveHistory(user: Customer) {
        val name = user.name
        println("$name's Transaction list: ")
        val y = user.tHistory
        for (x in y.indices) {
            println(y[x].toString())
        }
    }

    fun showAccs() {
        println("Account list:")
        val acc = customerList
        for (x in acc.indices) {
            println(acc[x].getAccdata())
        }
    }

    //withdraw features
    fun withdraw(user: Customer) {
        println("Your Balance is ${checkBal(user)} Petots")
        print("Enter 'e' to cancel or amount to withdraw: ")
        var isAmount = false
        //retries if invalid inputs. must an integer to be present
        while (!isAmount) {
            try {
                //amount is present to be deducted to balance
                val amount = readLine()
                //cancels the operation
                if (amount == "e") {
                    println("Withdraw cancelled!")
                    return
                } else {
                    //get balance amount of user
                    val wdAmount = amount?.toDouble()
                    val money = user.bal
                    //check if balance is less than to users balance
                    if ((money >= (amount?.toDouble()!!)) && wdAmount!! >= 0) {
                        //proceeds to deduct
                        val balance = money - (amount.toDouble())
                        //final output to users balance
                        user.bal = balance
                        println("$amount petots deducted")
                        println("Your Balance now is $balance Petots")
                        wdAmount.let { user.recordStart(it, "Deposit") }
                        isAmount = true
                    } else {
                        print("Invalid/Insufficient Balance. Please enter 'e' to cancel or value that not exceed to your balance: ")
                    }
                }
            } catch (e: Exception) {
                print("Invalid input. Enter 'e' to cancel or valid number: ")
            }
        }

    }

    //deposit features
    fun deposit(user: Customer) {
        print("Enter 'e' to cancel or amount to deposit: ")
        var isAmount = false
        //retries if invalid inputs and must integer
        while (!isAmount) {
            try {
                //amount of users entered
                val amount = readLine()
                //cancels the operation
                if (amount == "e") {
                    println("Deposit cancelled!")
                    return
                } else {
                    //reads the users balance
                    val cap: Int = amount!!.toInt()
                    if (cap >= 1000 && cap >= 0) {
                        val saveAmount = (amount.toDouble())
                        val money = user.bal
                        //proceeds to add balance
                        val balance = money + (amount.toDouble())
                        //final output to users balance
                        user.bal = balance
                        println("Added $amount Petots successful")
                        println("Your Balance is now ${checkBal(user)} Petots")
                        user.recordStart(saveAmount, "Deposit")
                        isAmount = true
                    } else {
                        print("Invalid input, please enter 'e' to cancel or more than 1000 petots: ")
                    }
                }
            } catch (e: Exception) {
                print("Invalid input. Please enter 'e' to cancel or a valid number: ")
            }
        }
    }

    //check bal features
    fun checkBal(user: Customer): Any {
        return df.format(user.bal).toDouble()
    }

    //displays log-in name customer
    fun dispName(user: Customer) {
        val dname = user.name
        return println("Welcome $dname")
    }

    //check valid pin feature
    fun getPin(pin: Int): Customer {
        var fPin = Customer(0, 0, "", "", 0.0, arrayListOf(saveHistory()))
        for (customer in customerList) {
            if (customer.pin == pin) { //finds the pin of user if match to the range
                fPin = customer
            }
        }
        return fPin
    }

    // send money features
    fun sendMoney(user: Customer) {
        var isAccountNumber = false
        var accNumber = ""
        var accountName = ""
        val money = user.bal
        // while account number is not present
        while (!isAccountNumber) {
            try {
                //retries if invalid inputs and must integer
                if (accNumber.isEmpty()) {
                    print("Enter 'e' to cancel or account number of receiver: ")
                    accNumber = readLine().toString()
                    //cancels the operation
                } else if (accNumber == "e") {
                    println("Transfer money cancelled!")
                    accountName = ""
                    return
                } else {
                    //check if account number is exist then.
                    val user2 = customerList.filterByAccn(accNumber.toInt()).last()
                    if (accountName.isEmpty()) {
                        //check if account name is match to account number then
                        print("Enter 'e' to cancel or account name of receiver: ")
                        accountName = readLine().toString()
                    } else if (accountName == "e") {
                        println("Transfer money cancelled!")
                        accountName = ""
                        return
                    } else {
                        //proceeds if account number and account name is match
                        if (user2.name.equals(accountName, ignoreCase = true)) {
                            try {
                                //Retries if invalid inputs and must integer
                                print("Enter 'e' to cancel or an amount send to ${user2.name}: ")
                                val amount = readLine()
                                if (amount == "e") {
                                    println("Transfer money cancelled!")
                                    return
                                }
                                //check if balance of user is less than present balance
                                else if (money >= (amount?.toDouble()!!)) {
                                    //deduct present balance to user
                                    val saveAmount = amount.toDouble()
                                    val balance = money - (amount.toDouble())
                                    //adds the balance sent to next user
                                    user2.bal = user2.bal + amount.toDouble()
                                    //final output of user balance
                                    user.bal = balance
                                    println("$amount petots is Transfer to ${user2.name} Successfully")
                                    println("Your Balance is now $balance Petots.")
                                    user.recordStart(saveAmount, "PtoP")
                                    isAccountNumber = true
                                } else {
                                    print("Insufficient Balance. Please enter value that not exceed to your balance: ")
                                }
                            } catch (e: Exception) {
                                print("Invalid input. Please input an amount: ")
                            }
                        } else {
                            println("Account Name of ${user2.accnumber} is incorrect")
                            accountName = ""
                        }
                    }
                }
            } catch (e: Exception) {
                println("User does not exist")
                accNumber = ""
            }
        }
    }
}
