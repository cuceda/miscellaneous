def amount_as_number:
    # Example
    # Command:  amount_as_number
    # Input:    {"currency": "EUR", "amount": 10050, "decimals": 2}
    # Output:   {"currency": "EUR", "amount": 100.5}

    if .amount and .decimals
    then .amount = ("\(.amount)e-\(.decimals)" | tonumber) | del(.decimals)
    else . end;


def ascii_titlecase:
    # Example
    # Command:  ascii_titlecase
    # Input:    "abc"
    # Output:   "Abc"

    (.[0:1] | ascii_upcase) + (.[1:] | ascii_downcase);


def snake_to_camelcase:
    # Example
    # Command:  snake_to_camelcase
    # Input:    "tic_tac_toe"
    # Output:   "ticTacToe"

    split("_")
    | map(select(length > 0))
    | (.[0:1] | map(ascii_downcase)) + (.[1:] | map(ascii_titlecase))
    | join("");


def aws_dynamodb_attr:
    # Example (1)
    # Command:  aws_dynamodb_attr
    # Input:    {"S": "Hello"}
    # Output:   "Hello"
    # Example (2)
    # Command:  aws_dynamodb_attr
    # Input:    {"NS": ["42.2", "-19", "7.5", "3.14"]}
    # Output:   [42.2, -19, 7.5, 3.14]
    # Example (3)
    # Command:  aws_dynamodb_attr
    # Input:    {"L": [ {"S": "Cookies"} , {"S": "Coffee"}, {"N": "3.14159"}, {"NULL": true}]}
    # Output:   ["Cookies", "Coffee", 3.14159, null]
    # Example (4)
    # Command:  aws_dynamodb_attr
    # Input:    {"M": {"Name": {"S": "Joe"}, "Age": {"N": "35"}}}
    # Output:   {"Name": "Joe", "Age": 35}

    . as $input
    | to_entries[0]
    | if .key == "NULL" and .value == true then null
    elif .key == "N" then .value | tonumber
    elif .key == "NS" then .value | map(tonumber)
    elif .key == "L" then .value | map(aws_dynamodb_attr)
    elif .key == "M" then .value | map_values(aws_dynamodb_attr)
    elif IN(.key; "B", "BOOL", "BS", "S", "SS") then .value
    else error("Illegal DynamoDB attribute value: " + ($input | tojson))
    end;


def aws_dynamodb_item:
    # Example
    # Command:  aws_dynamodb_item
    # Input:    {"Id": {"S":"12345A"}, "ReplyInstant": {"N": "1424217600"}, "PostedBy": {"S": "Thomas"}}
    # Output:   {"Id": "12345A", "ReplyInstant": 1424217600, "PostedBy": "Thomas"}

    map_values(aws_dynamodb_attr);
