package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"github.com/gorilla/websocket"
	"log"
	"net/http"
	"os"
	"os/exec"
)

var upgrader = websocket.Upgrader{
	ReadBufferSize:  2048,
	WriteBufferSize: 2048,
	CheckOrigin:     func(r *http.Request) bool { return true },
}
var list = []string{}

/*type Client struct {
	conn *websocket.Conn
}*/

var arr = []*websocket.Conn{}

/*
func (e Client) Init(conn1 *websocket.Conn) {
	e.conn=conn1
}*/

func addTicket(string2 string) {
	for i := 0; i < len(list); i++ {
		if string2 == list[i] {
			return
		}

	}
	strings := append(list, string2)
	list = strings
}

func showme() {
	fmt.Println("Server läuft ..")
	if len(list) == 0 {
		fmt.Println("Keine Tickets")
	} else {
		listjson, _ := json.Marshal(list)
		fmt.Println(string(listjson))
	}

}

func homePage(w http.ResponseWriter, r *http.Request) {
	upgrader.CheckOrigin = func(r *http.Request) bool { return true }

	showme()
	scanner := bufio.NewScanner(os.Stdin)
	fmt.Printf("n:neues Ticket  ,  q:quit")
	scanner.Scan()
	input := scanner.Text()
	if input == "n" {
		scanner := bufio.NewScanner(os.Stdin)
		fmt.Printf("Bitte Ticketnummer eingeben :")
		scanner.Scan()
		input1 := scanner.Text()
		addTicket(input1)

		for j := 0; j < len(arr); j++ {
			arr[j].WriteMessage(1, []byte("the Ticketlist in messages below :"))
			for i := 0; i < len(list); i++ {
				arr[j].WriteMessage(websocket.TextMessage, []byte(list[i]))

			}
		}

		homePage(w, r)

	} else if input == "q " {
		//close server or ..
		if err := exec.Command("cmd", "/C", "shutdown", "/s").Run(); err != nil {
			fmt.Println("Failed to initiate shutdown:", err)
		}
		//return

	}

}

type data struct {

	// defining struct variables
	ID     string `json:"id"`
	TICKET string `json:"ticket"`
}

func reader(conn *websocket.Conn) {
	for {

		messageType, p, err := conn.ReadMessage()
		if err != nil {

			log.Println(err)

			return
		}

		var dtest data
		log.Println(string(p))

		err = json.Unmarshal(p, &dtest)
		if err != nil {

			fmt.Println(err)

		}

		if dtest.TICKET != "" {
			addTicket(string(dtest.TICKET))
			fmt.Println("this client ", dtest.ID, "added a Ticket to server :")
		}
		/*conn.WriteMessage(messageType, []byte("the Ticketlist in messages below :"))
		for i := 0; i < len(list); i++ {
			conn.WriteMessage(messageType, []byte(list[i]))
		}*/
		for j := 0; j < len(arr); j++ {
			arr[j].WriteMessage(1, []byte("the Ticketlist in messages below :"))
			for i := 0; i < len(list); i++ {
				arr[j].WriteMessage(websocket.TextMessage, []byte(list[i]))

			}
		}

		showme()
		//var  = json.Marshal(p)
		//addTicket(json.Marshal(p))

		if err := conn.WriteMessage(messageType, p); err != nil {
			log.Println(err)
			return
		}

	}
}
func app(ar []*websocket.Conn, conn *websocket.Conn) {
	ar = append(ar, conn)

}
func wsEP(w http.ResponseWriter, r *http.Request) {
	upgrader.CheckOrigin = func(r *http.Request) bool { return true }

	ws, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Println(err)
	}
	log.Println("Client successfully Connected..")

	arr = append(arr, ws)
	//app(arr, ws)
	reader(ws)
}

func Routing() {
	http.HandleFunc("/", homePage)
	http.HandleFunc("/ws", wsEP)

	//http.HandleFunc("/ws/client1.html", wsEP)
}

func main() {
	go fmt.Println(" please open localhost:8080")

	go Routing()
	go log.Fatal(http.ListenAndServe(":8080", nil))

}
