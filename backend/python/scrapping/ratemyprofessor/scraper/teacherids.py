import requests
import json

# URL for the RateMyProfessors GraphQL API
url = "https://www.ratemyprofessors.com/graphql"

# Headers for the request
headers = {
    "Content-Type": "application/json",
    "Authorization": "Basic dGVzdDp0ZXN0",  # Base64 encoded username:password
    "Origin": "https://www.ratemyprofessors.com",
    "Referer": "https://www.ratemyprofessors.com/search/professors/1422?q=*",
    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36",
    "Sec-Fetch-Dest": "empty",
    "Sec-Fetch-Mode": "cors",
    "Sec-Fetch-Site": "same-origin",
    "Accept": "*/*",
    "Accept-Encoding": "gzip, deflate, br, zstd",
    "Accept-Language": "en-US,en;q=0.9",
    "Dnt": "1"
}

# GraphQL query and variables
payload = {
    "query": """
    query TeacherSearchResultsPageQuery($query: TeacherSearchQuery!, $schoolID: ID, $includeSchoolFilter: Boolean!) {
      search: newSearch {
        ...TeacherSearchPagination_search_1ZLmLD
      }
      school: node(id: $schoolID) @include(if: $includeSchoolFilter) {
        __typename
        ... on School {
          name
        }
        id
      }
    }

    fragment TeacherSearchPagination_search_1ZLmLD on newSearch {
      teachers(query: $query, first: 3658, after: "") {
        didFallback
        edges {
          cursor
          node {
            ...TeacherCard_teacher
            id
            __typename
          }
        }
        pageInfo {
          hasNextPage
          endCursor
        }
        resultCount
        filters {
          field
          options {
            value
            id
          }
        }
      }
    }

    fragment TeacherCard_teacher on Teacher {
      id
      legacyId
      avgRating
      numRatings
      ...CardFeedback_teacher
      ...CardSchool_teacher
      ...CardName_teacher
      ...TeacherBookmark_teacher
    }

    fragment CardFeedback_teacher on Teacher {
      wouldTakeAgainPercent
      avgDifficulty
    }

    fragment CardSchool_teacher on Teacher {
      department
      school {
        name
        id
      }
    }

    fragment CardName_teacher on Teacher {
      firstName
      lastName
    }

    fragment TeacherBookmark_teacher on Teacher {
      id
      isSaved
    }
    """,
    "variables": {
        "query": {
            "text": "",
            "schoolID": "U2Nob29sLTE0MjI=",
            "fallback": True,
            "departmentID": None
        },
        "schoolID": "U2Nob29sLTE0MjI=",
        "includeSchoolFilter": True
    }
}

# Making the POST request
response = requests.post(url, headers=headers, json=payload)

# Check the status code and print the response data
if response.status_code == 200:
    with open('../data/output/concordiaProfRateMyProf.json', 'w') as file:
        json.dump(response.json(), file, indent=4)
    print("Data written to 'concordiaProfRateMyProf.json'")
else:
    print("Failed to fetch data: ", response.status_code)
