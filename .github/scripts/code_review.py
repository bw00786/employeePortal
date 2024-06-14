import os
import requests
from transformers import AutoTokenizer, AutoModelForCausalLM
import torch
rom huggingface_hub import login



GITHUB_TOKEN = os.getenv('GITHUB_TOKEN')
HUGGINGFACE_HUB_TOKEN = os.getenv('HUGGINGFACE_HUB_TOKEN')
MISTRAL_MODEL_NAME = 'mistralai/mistral-7b-instruct-v0.3'

# Hugging Face Login (use either environment variable or direct token)
login(token=os.environ.get('HUGGINGFACE_HUB_TOKEN', HUGGINGFACE_HUB_TOKEN))

# Load the Mistral model and tokenizer
tokenizer = AutoTokenizer.from_pretrained(MISTRAL_MODEL_NAME)
model = AutoModelForCausalLM.from_pretrained(MISTRAL_MODEL_NAME)

def get_pr_files(repo, pr_number):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/files"
    headers = {'Authorization': f'token {GITHUB_TOKEN}'}
    response = requests.get(url, headers=headers)
    response.raise_for_status()
    return response.json()

def call_mistral_llm(filename, patch):
    prompt = f"Review the following code changes in {filename}:\n{patch}\nProvide any suggestions or improvements on the basis of security, logic errors."

    inputs = tokenizer(prompt, return_tensors="pt")
    outputs = model.generate(**inputs, max_new_tokens=750)
    review_comment = tokenizer.decode(outputs[0], skip_special_tokens=True)

    return review_comment

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
