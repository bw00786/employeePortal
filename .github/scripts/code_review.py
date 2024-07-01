import os
import requests
import logging
from langchain_huggingface import HuggingFaceEndpoint
from langchain_core.prompts import PromptTemplate
from langchain.chains import LLMChain
from huggingface_hub import login

# Set up logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Load tokens from environment variables
GITHUB_TOKEN = os.getenv('GITHUB_TOKEN')
HUGGINGFACE_HUB_TOKEN = os.getenv('HUGGINGFACE_HUB_TOKEN')
MISTRAL_MODEL_NAME = 'mistralai/Mistral-7B-Instruct-v0.3'

# Ensure the Hugging Face token is available
if not HUGGINGFACE_HUB_TOKEN:
    raise ValueError("HUGGINGFACE_HUB_TOKEN environment variable not set")

# Hugging Face Login
login(token=HUGGINGFACE_HUB_TOKEN)

# Initialize the Hugging Face endpoint
llm = HuggingFaceEndpoint(repo_id=MISTRAL_MODEL_NAME, max_length=750, temperature=0.7, token=HUGGINGFACE_HUB_TOKEN)

# Define the prompt template
template = """Review the following code changes in {filename}:
{patch}
Provide any suggestions or improvements on the basis of security, logic errors."""
prompt = PromptTemplate(template=template, input_variables=["filename", "patch"])
llm_chain = LLMChain(llm=llm, prompt=prompt)

def get_pr_files(repo, pr_number):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/files"
    headers = {'Authorization': f'token {GITHUB_TOKEN}'}
    response = requests.get(url, headers=headers)
    response.raise_for_status()
    return response.json()

def call_mistral_llm(filename, patch):
    response = llm_chain.invoke({"filename": filename, "patch": patch})
    return response

def post_review_comments(repo, pr_number, comments):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/reviews"
    headers = {'Authorization': f'token {GITHUB_TOKEN}'}
    data = {
        'body': 'Automated code review comments',
        'event': 'COMMENT',
        'comments': comments
    }
    response = requests.post(url, json=data, headers=headers)
    response.raise_for_status()
    return response.json()

def main():
    repo = os.getenv('GITHUB_REPOSITORY')
    pr_number = os.getenv('PR_NUMBER')

    files = get_pr_files(repo, pr_number)
    comments = []
    for file in files:
        filename = file['filename']
        patch = file.get('patch', '')
        if patch:
            review_comment = call_mistral_llm(filename, patch)
            if review_comment:
                comments.append({
                    'path': filename,
                    'position': 1,
                    'body': review_comment
                })

    post_review_comments(repo, pr_number, comments)

if __name__ == "__main__":
    main()


