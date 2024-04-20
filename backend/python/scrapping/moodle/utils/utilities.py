import re
import json


def normalize_text(text):
    """ Normalize the text by removing all non-alphanumeric characters, except for critical ones. """
    return re.sub(r'[^a-zA-Z0-9/]+', '', text)


def extract_ids_from_text(text):
    """ Extract potential IDs from the given text field. Adjust regex to capture merged text. """
    # Attempt to split merged words and normalize text to handle 'Summer2021' cases
    text = normalize_text(text)
    return set(re.findall(r'([A-Z]+[0-9]+[A-Z0-9/]*)', text))


def longest_common_start(id1, id2):
    """ Return the longest common starting substring from two strings. """
    min_length = min(len(id1), len(id2))
    result = []
    for i in range(min_length):
        if id1[i] == id2[i]:
            result.append(id1[i])
        else:
            break
    return ''.join(result)


def load_json(filepath):
    """ Load data from a JSON file. """
    with open(filepath, 'r') as file:
        data = json.load(file)
    return data


def save_json(data, filepath):
    """ Save data to a JSON file. """
    with open(filepath, 'w') as file:
        json.dump(data, file, indent=4)


def check_missing_ids(valid_ids, used_ids, not_available_ids):
    all_valid_ids = set()
    for prefix, ids in valid_ids.items():
        for id in ids:
            all_valid_ids.add(prefix + id)

    all_used_ids = used_ids.union(set(not_available_ids))
    missing_ids = all_valid_ids.difference(all_used_ids)

    if missing_ids:
        print(f"Missing IDs:({len(missing_ids)})", missing_ids)
    else:
        print("All IDs are accounted for.")
